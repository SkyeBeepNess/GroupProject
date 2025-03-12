package attendancePage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

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
    
    public static List<Student> getStudentsForCourse(String courseID) {
        List<Student> students = new ArrayList<>();
        Map<Date, String> studAttendanceList = new HashMap<Date, String>();
        String sql = "SELECT StudentID, attendance, SessionDate FROM Attendance WHERE CourseID = '" + courseID + "'";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        HashMap<String, Student> studentMap = new HashMap<>();
        
        
        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
            	String studentID = rs.getString("StudentID");
                Date sessionDate = sdf.parse(rs.getString("SessionDate"));
                String attended = rs.getString("Attendance");
                Student student = studentMap.getOrDefault(studentID, new Student(studentID));
                student.addAttendanceRecord(sessionDate, attended);

                // Add student back to map
                studentMap.put(studentID, student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(studentMap.values());
    
    }
}
