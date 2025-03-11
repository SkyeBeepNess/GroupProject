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
        String sql = "SELECT StudentID FROM Attendance WHERE CourseID = 'BMS'";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                students.add(new Student(
                        rs.getString("StudentID")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
}
