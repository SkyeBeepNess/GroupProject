package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Student;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataBaseHelper {
	private static final String URL = "jdbc:sqlite:src/resources/database/UNIMAN.db";
	
	public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static HashMap<LocalDate, String> fetchAllAttendanceRecordsByStudent(String studentID) {
    	HashMap<LocalDate, String> attendanceRecordsMap = new HashMap<>();
    	String sql = "SELECT courseID, attendance, SessionDate FROM Attendance WHERE StudentID = '" + studentID + "'";
    	try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate sessionDate = LocalDate.parse(rs.getString("SessionDate"), formatter);
                    
                    String attended = rs.getString("Attendance");
                    attendanceRecordsMap.put(sessionDate, attended);

                }
            } catch (Exception e) {
                e.printStackTrace();
        }
    	return attendanceRecordsMap;
	}
    public static List<String> getAllCourseIDs() {
        List<String> courseIDs = new ArrayList<>();
        String sql = "SELECT DISTINCT CourseID FROM attendance";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courseIDs.add(rs.getString("CourseID"));
                 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseIDs;
    }
    
    public static List<Student> getStudentsForCourse(String courseID, List<LocalDate> dateRange, String searchInput) {
    	String sql = null;
    	List<String> courses = new ArrayList<String>();
    	if (searchInput == null && courseID != null) {
    		sql = "SELECT StudentID, attendance, SessionDate FROM Attendance WHERE CourseID = '" + courseID + "'";
		}
    	else if (searchInput != null && courseID != null) {
    		sql = "SELECT StudentID, attendance, SessionDate FROM Attendance WHERE CourseID = '" + courseID + "' AND StudentID like '%" + searchInput + "'" ;
		}
    	else if (courseID == null && searchInput != null) {
    		courses = getAllCourseIDs();
    		for (int i = 0; i < getAllCourseIDs().size(); i++) {
				getStudentsForCourse(courses.get(i), dateRange, searchInput);
			}
    		sql = "SELECT StudentID, attendance, SessionDate FROM Attendance WHERE CourseID = '" + courseID + "' AND StudentID like '%" + searchInput + "'" ;
		}
    		
        Map<String, Student> studentMap = new HashMap<>();
        
        
        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
            	String studentID = rs.getString("StudentID");

                Student student = studentMap.get(studentID);
                
              
                if (student == null) {
                	student = new Student(studentID);
                	studentMap.put(studentID, student);
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(studentMap.values());
    
    }
}
