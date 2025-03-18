package dbhandlers;

import models.Applicant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                String fullName = rs.getString("Applicant Name");
                String[] nameParts = fullName.split(" ");
                String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
                String firstName = fullName.replace(" " + lastName, ""); 

                return new Applicant(
                    rs.getString("UserID"),
                    rs.getString("ApplicationID"),
                    rs.getString("Date of Application"),
                    firstName,
                    lastName, 
                    rs.getString("Nationality"),
                    rs.getString("DateOfBirth"),
                    rs.getInt("ukprn"),
                    rs.getString("Certificate"),
                    rs.getString("PreviousInstitution"),
                    rs.getString("Grade"),
                    rs.getString("DateOfCompletion"),
                    rs.getString("PassportPath"),
                    rs.getString("DiplomaPath"),
                    rs.getString("Status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updatePassportPath(String userId, String passportPath) {
        String sql = """
            INSERT INTO applicants (UserID, PassportPath) 
            VALUES (?, ?)
            ON CONFLICT(UserID) DO UPDATE SET PassportPath = excluded.PassportPath
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, passportPath);
            int updatedRows = stmt.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateDiplomaPath(String userId, String diplomaPath) {
        String sql = """
            INSERT INTO applicants (UserID, DiplomaPath) 
            VALUES (?, ?)
            ON CONFLICT(UserID) DO UPDATE SET DiplomaPath = excluded.DiplomaPath
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, diplomaPath);
            int updatedRows = stmt.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
   
    
    public List<Applicant> getAllApplicants() {
        List<Applicant> applicants = new ArrayList<>();
        String sql = "SELECT * FROM applicants";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                applicants.add(new Applicant(
                        rs.getString("UserID"),
                        rs.getString("Applicant Name"),
                        rs.getString("ApplicationID"),
                        rs.getString("Date of Application"),
                        rs.getString("Certificate"),
                        rs.getString("Grade"),
                        rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applicants;
    }
    
    public boolean updatePersonalDetails(Applicant applicant) {
        String sql = "UPDATE applicants SET `Applicant Name` = ?, Nationality = ?, DateOfBirth = ? WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, applicant.getName() + " " + applicant.getLastName());
            stmt.setString(2, applicant.getNationality());
            stmt.setString(3, applicant.getDob());
            stmt.setString(4, applicant.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAcademicQualification(Applicant applicant) {
        String sql = "UPDATE applicants SET Certificate = ?, PreviousInstitution = ?, Grade = ?, DateOfCompletion = ? WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, applicant.getCertificate());
            stmt.setString(2, applicant.getPreviousInstitution());
            stmt.setString(3, applicant.getGrade());
            stmt.setString(4, applicant.getDocDate());
            stmt.setString(5, applicant.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
