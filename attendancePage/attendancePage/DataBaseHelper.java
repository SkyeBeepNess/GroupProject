package attendancePage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
    	System.out.println("test");
        List<String> courseIDs = new ArrayList<>();
        String sql = "SELECT DISTINCT CourseID FROM attendance";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courseIDs.add(rs.getString("CourseID"));
                System.out.println("test");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseIDs;
    }
    
    public static List<Student> getStudentsForCourse(String courseID) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT StudentID, Attendance FROM Attendance WHERE CourseID = BMS";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                students.add(new Student(
                        rs.getString("StudentID"),
                        rs.getString("Attendance")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
}
