package dbhandlers;

import models.Applicant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApplicantDAO {
    private final Connection connection;

    public ApplicantDAO() {
        this.connection = DataBaseManager.getInstance().getConnection();
    }

    public Applicant getApplicantByUserId(String userId) {
        String sql = "SELECT * FROM applicants WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Applicant(
                    rs.getString("UserID"),
                    rs.getString("ApplicationID"),
                    rs.getString("Date"),
                    rs.getString("Applicant Name"),
                    rs.getString("Certificate"),
                    rs.getString("Grade"),
                    rs.getInt("UKPRN"),
                    rs.getString("Status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
