package university.management;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:appl.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Connection to database failed: " + e.getMessage());
            return null;
        }
    }

    public static ObservableList<Applicant> getAllApplicants() {
        ObservableList<Applicant> applicants = FXCollections.observableArrayList();
        String sql = "SELECT * FROM applicants";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                applicants.add(new Applicant(
                        rs.getString("Applicant Name"),
                        rs.getString("Date of Application"),
                        rs.getString("Certificate"),
                        rs.getString("Grade"),
                        rs.getString("Status") 
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving applicants: " + e.getMessage());
        }
        return applicants;
    }
    
    public static ObservableList<Applicant> getApplicantByName(String applicantName) {
        ObservableList<Applicant> applicants = FXCollections.observableArrayList();
        String sql = "SELECT * FROM applicants WHERE `Applicant Name` = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, applicantName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                applicants.add(new Applicant(
                        rs.getString("Applicant Name"),
                        rs.getString("Date of Application"),
                        rs.getString("Certificate"),
                        rs.getString("Grade"),
                        rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving applicant data: " + e.getMessage());
        }
        return applicants;
    }
    
    public static void updateApplicationStatus(String applicantName, String newStatus) {
        String sql = "UPDATE applicants SET Status = ? WHERE `Applicant Name` = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, applicantName);
            stmt.executeUpdate();

            System.out.println("Application " + applicantName + " updated to: " + newStatus);
        } catch (SQLException e) {
            System.out.println("Error updating status: ");
        }
    }

}
