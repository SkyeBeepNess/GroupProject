package dbhandlers;

import models.Applicant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicantDAO {
    private static Connection connection = null;

    public ApplicantDAO() {
        ApplicantDAO.connection = DataBaseManager.getInstance().getConnection();
    }

    public static Applicant getApplicantByUserId(String userId) {
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
                    rs.getString("ukprn"),
                    rs.getString("Certificate"),
                    rs.getString("PreviousInstitution"),
                    rs.getString("Grade"),
                    rs.getString("DateOfCompletion"),
                    rs.getString("PassportPath"),
                    rs.getString("DiplomaPath"),
                    rs.getString("Status"),
                    rs.getString("ProfilePicture")
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
                        rs.getString("ukprn"),
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
    
    public List<Applicant> getApplicantsBy(String stDate, String enDate, List<String> ukprns, String search) {
        List<Applicant> applicants = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT UserID, "Applicant Name", ApplicationID, "Date of Application", Certificate, Grade, UKPRN, Status 
            FROM applicants WHERE 1=1
        """);

        List<Object> parameters = new ArrayList<>();

        if (stDate != null && !stDate.isBlank() && enDate != null && !enDate.isBlank()) {
        	sql.append("""
        		    AND DATE(substr("Date of Application", 7, 4) || '-' || substr("Date of Application", 4, 2) || '-' || substr("Date of Application", 1, 2))
        		    BETWEEN DATE(?) AND DATE(?)
        		""");
            parameters.add(stDate);
            parameters.add(enDate);
        } else if (stDate != null && !stDate.isBlank()) {
            sql.append(" AND DATE(substr(\"Date of Application\", 7, 4) || '-' || substr(\"Date of Application\", 4, 2) || '-' || substr(\"Date of Application\", 1, 2)) >= ?");
            parameters.add(stDate);
        } else if (enDate != null && !enDate.isBlank()) {
            sql.append(" AND DATE(substr(\"Date of Application\", 7, 4) || '-' || substr(\"Date of Application\", 4, 2) || '-' || substr(\"Date of Application\", 1, 2)) <= ?");
            parameters.add(enDate);
        }

        if (ukprns != null && !ukprns.isEmpty()) {
            sql.append(" AND UKPRN IN (");
            sql.append(String.join(",", Collections.nCopies(ukprns.size(), "?")));
            sql.append(")");
            parameters.addAll(ukprns);
        }
        
        if (search != null && !search.isBlank()) {
            sql.append(" AND (\"Applicant Name\" LIKE ? OR ApplicationID LIKE ?)");
            String searchPattern = "%" + search + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                applicants.add(new Applicant(
                    rs.getString("UserID"),
                    rs.getString("Applicant Name"),
                    rs.getString("ApplicationID"),
                    rs.getString("Date of Application"),
                    rs.getString("Certificate"),
                    rs.getString("Grade"),
                    rs.getString("UKPRN"),
                    rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return applicants;
    }

    public boolean updateProfilePicture(String userId, String base64) {
        String sql = "UPDATE applicants SET ProfilePicture = ? WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, base64);
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
