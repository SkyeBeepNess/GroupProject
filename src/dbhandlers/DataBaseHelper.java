package dbhandlers;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Student;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataBaseHelper {
	private static Connection connection;

    public DataBaseHelper() {
        this.connection = DataBaseManager.getInstance().getConnection();
    }
	

    public static List<String> getAllCourseIDs() {
        List<String> courseIDs = new ArrayList<>();
        String sql = "SELECT DISTINCT CourseID FROM attendance";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courseIDs.add(rs.getString("CourseID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseIDs;
    }    
    
    public static Student getStudentByUserID(String userID) {
    	StringBuilder sql = new StringBuilder("SELECT a.UserID, u.Name, a.CourseID, a.StudentID, a.Attendance, a.SessionDate FROM attendance a JOIN users u ON a.UserID = u.UserID WHERE a.UserID == ?");
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	Student student = null;
    	try (
                PreparedStatement stmt = connection.prepareStatement(sql.toString())) {  
                stmt.setObject(1, userID);
                
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String studentID = rs.getString("StudentID");
                    String courseID = rs.getString("CourseID");
                    String name = rs.getString("Name");
                    student = new Student(studentID, courseID, name);
				}
                do {
                	
                    String attendance = rs.getString("Attendance");
                    
                    LocalDate sessionDate = LocalDate.parse(rs.getString("SessionDate"), formatter);
                    student.addAttendance(sessionDate, attendance);
                    
                } while (rs.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
    	return student;
	}
    
    public static List<Student> getStudentsForCourse(List<String> courseIDs, List<LocalDate> dateRange, String searchInput) {
        Map<String, Student> studentMap = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT a.UserID, u.Name, a.CourseID, a.StudentID, a.Attendance, a.SessionDate FROM attendance a JOIN users u ON a.UserID = u.UserID");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Object> parameters = new ArrayList<>();

        // ✅ Add CourseID filtering dynamically
        if (courseIDs != null && !courseIDs.isEmpty()) {
            sql.append(" WHERE CourseID IN (");
            sql.append(String.join(",", Collections.nCopies(courseIDs.size(), "?")));
            sql.append(")");
            parameters.addAll(courseIDs);
        }

        // ✅ Add searchInput condition if provided
        if (searchInput != null) {
            sql.append(parameters.isEmpty() ? " WHERE (" : " AND (").append("a.StudentID LIKE ? OR u.Name LIKE ?)");
            parameters.add("%" + searchInput + "%");
            parameters.add("%" + searchInput + "%");
        }

        // ✅ Add date range condition if provided
        if (dateRange != null && dateRange.size() == 2) {
        	
            sql.append(parameters.isEmpty() ? " WHERE " : " AND ").append("DATE(substr(SessionDate, 7, 4) || '-' || substr(SessionDate, 4, 2) || '-' || substr(SessionDate, 1, 2)) BETWEEN ? AND ?");
            parameters.add(String.valueOf(dateRange.get(0)));
            parameters.add(String.valueOf(dateRange.get(1)));
        }
        
        try (
            PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	String courseID = rs.getString("courseID");
                String studentID = rs.getString("StudentID");
                String attendance = rs.getString("Attendance");
                String name = rs.getString("Name");
                LocalDate sessionDate = LocalDate.parse(rs.getString("SessionDate"), formatter);

                studentMap.computeIfAbsent(studentID, k -> new Student(studentID, courseID, name)).addAttendance(sessionDate, attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(studentMap.values());
    }
    
  
    
    public static Map<LocalDate, String> getAttendanceRecords(String studentID) {
        Map<LocalDate, String> records = new HashMap<>();
        String sql = "SELECT SessionDate, Attendance FROM attendance WHERE StudentID = ?";

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.put(rs.getDate("SessionDate").toLocalDate(), rs.getString("Attendance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }



    
}
