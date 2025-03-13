package attendancePage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataBaseHelper {
	private static final String URL = "jdbc:sqlite:resources/UNIMAN.db";

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
                	
                	System.out.println("Hello");
                	//System.out.println(rs.getDate("SessionDate"));
                	System.out.println(LocalDate.parse(rs.getString("SessionDate"), formatter));
                	System.out.println("bye");
                    //LocalDate sessionDate = rs.getDate("SessionDate").toLocalDate();
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
    
    public static List<Student> getStudentsForCourse(String courseID, List<LocalDate> dateRange) {
        String sql = "SELECT StudentID, attendance, SessionDate FROM Attendance WHERE CourseID = '" + courseID + "'";
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
