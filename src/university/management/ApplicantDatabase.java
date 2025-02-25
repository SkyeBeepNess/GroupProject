package university.management;
import java.sql.*;

public class ApplicantDatabase {
    private static final String DB_URL = "jdbc:sqlite:appl.db";
    
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Connection to database failed: " + e.getMessage());
            return null;
        }
    }

    public static void displayApplicants() {
        String sql = "SELECT * FROM applicants";
        
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Applicants List:");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("Applicant Name") + 
                                   ", Date: " + rs.getString("Date of Application") +
                                   ", Certificate: " + rs.getString("Certificate") +
                                   ", Grade: " + rs.getString("Grade"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving applicants: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        displayApplicants();
    }
}
