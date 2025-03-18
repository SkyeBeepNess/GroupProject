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
                    rs.getString("Date of Application"),
                    rs.getString("Applicant Name"),
                    rs.getString("Certificate"),
                    rs.getString("Grade"),
                    rs.getInt("UKPRN"),
                    rs.getString("Status"),
                    rs.getString("PreviousInstitution"),
                    rs.getString("PassportPath"),
                    rs.getString("DiplomaPath")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassportPath(String userId, String passportPath) {
        String sql = "UPDATE applicants SET PassportPath = ? WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, passportPath);
            stmt.setString(2, userId);
            int updatedRows = stmt.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDiplomaPath(String userId, String diplomaPath) {
        String sql = "UPDATE applicants SET DiplomaPath = ? WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, diplomaPath);
            stmt.setString(2, userId);
            int updatedRows = stmt.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
