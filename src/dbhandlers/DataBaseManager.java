package dbhandlers;


import session.UserSession;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.scene.control.Alert.AlertType;
import models.Admin;
import services.UIServices;

public class DataBaseManager {

    private static DataBaseManager instance;
    private static Connection connection;
    
    private DataBaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/resources/database/UNIMAN.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }

    
    public boolean userExists(String username) {
        String sql = "SELECT UserID FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean addUser(String userId, String name, String username, String password, String role) {
        String sql = "INSERT INTO users (UserID, Name, username, password, Role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, username);
            stmt.setString(4, password);
            stmt.setString(5, role);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean getUserByUsernameAndPassword(String username, String password) {
        String sql = "SELECT UserID, username, Role FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("UserID");
                String role = rs.getString("Role");
                UserSession.createSession(userId, username, role);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static List<String> getAdminUKPRNbyUserID(String userID, String role){
    	List<String> ukprns = new ArrayList<>();
    	String sql;
    	boolean isSuperadmin = "superadmin".equals(role);
    	if (isSuperadmin) {
    		sql = "SELECT DISTINCT ukprn FROM institution";
    	}
    	else {
    		sql = "SELECT ukprn FROM admin_university_access WHERE userId = ?";
    	}
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())){
        	if (!isSuperadmin) {
        		stmt.setObject(1, userID);
        	}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ukprns.add(rs.getString("ukprn"));
			}
			
		} catch (Exception e) {
            e.printStackTrace();
		}
        return ukprns;
    }
    
    public static String generateNewUserId() {
        String newUserId = "000000000005";
        try {
            String sql = "SELECT MAX(UserID) FROM users";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getString(1) != null) {
                long maxId = Long.parseLong(rs.getString(1)) + 1;
                newUserId = String.format("%012d", maxId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newUserId;
    }
    
    public static boolean loadApplicantsFromCSV(String csvFilePath) {
        String insertApplicantSQL = "INSERT INTO applicants (\"Applicant Name\", \"Date of Application\", \"Certificate\", \"Grade\", \"UKPRN\", \"ApplicationID\", \"Status\", \"UserID\") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        UserSession session = UserSession.getInstance();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             PreparedStatement pstmtApplicant = connection.prepareStatement(insertApplicantSQL)) {

            connection.setAutoCommit(false);
            Random random = new Random();
            String line;
            if ((line = br.readLine()) != null) {
            	line = line.replace("\uFEFF", "").trim();
            	System.out.println("CSV Header: " + line);
            	String[] expectedHeaders = {"Applicant Name", "Date of Application", "Certificate", "Grade"};
            	String[] colNames = line.split(",");

            	if (colNames.length < expectedHeaders.length) {
            	    UIServices.showAlert(AlertType.ERROR, "File import error", "File must contain the 4 required columns.");
            	    return false;
            	}

            	for (int i = 0; i < expectedHeaders.length; i++) {
            	    String actual = colNames[i].trim().replaceAll("\"", "");
            	    if (!actual.equalsIgnoreCase(expectedHeaders[i])) {
            	        UIServices.showAlert(AlertType.ERROR, "File import error", "Missing or incorrect column: " + expectedHeaders[i]);
            	        return false;
            	    }
            	}
            }
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length < 4) continue;

                String applicantName = values[0].trim();
                String dateOfApplication = values[1].trim();
                String certificate = values[2].trim();
                String grade = values[3].trim();

                String ukprn = getAdminUKPRNbyUserID(UserSession.getInstance().getUserId(), UserSession.getInstance().getRole()).get(0);

                String applicationID = ukprn + String.valueOf(100000 + random.nextInt(900000));

                String status = "Pending";

                String userID = generateNewUserId();

                pstmtApplicant.setString(1, applicantName);
                pstmtApplicant.setString(2, dateOfApplication);
                pstmtApplicant.setString(3, certificate);
                pstmtApplicant.setString(4, grade);
                pstmtApplicant.setString(5, ukprn);
                pstmtApplicant.setString(6, applicationID);
                pstmtApplicant.setString(7, status);
                pstmtApplicant.setString(8, userID);
                pstmtApplicant.addBatch();
                
                String baseUsername = applicantName.toLowerCase().replace(" ", "_");
                String finalUsername = baseUsername;
                int counter = 1;
                while (getInstance().userExists(finalUsername)) {
                    finalUsername = baseUsername + "_" + counter++;
                }

                addUser(userID, applicantName, finalUsername, userID.substring(userID.length() - 6), "applicant");
            }

            pstmtApplicant.executeBatch();
            connection.commit();
            generateUniqueUsernamesAndInsertApplicantsIntoUsers();

            System.out.println("Data base updated!");
            return true;

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void generateUniqueUsernamesAndInsertApplicantsIntoUsers() throws SQLException {
        String addColumnSQL = "ALTER TABLE applicants ADD COLUMN username_temp TEXT";
        String updateUsernameSQL = """
            UPDATE applicants 
            SET username_temp = ( 
                SELECT LOWER(REPLACE("Applicant Name", ' ', '_')) || '_' || 
                ROW_NUMBER() OVER (PARTITION BY "Applicant Name" ORDER BY UserID) 
                FROM applicants 
                WHERE applicants.UserID = applicants.UserID
            )
        """;
        String insertUsersSQL = """
            INSERT INTO users (UserID, Name, Role, username, password) 
            SELECT 
                UserID, 
                "Applicant Name", 
                'applicant', 
                username_temp, 
                SUBSTR(UserID, -6) 
            FROM applicants 
            WHERE NOT EXISTS (
                SELECT 1 FROM users u WHERE u.UserID = applicants.UserID 
                OR u.username = applicants.username_temp
            )
        """;
        String dropColumnSQL = "ALTER TABLE applicants DROP COLUMN username_temp";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(addColumnSQL);
            stmt.execute(updateUsernameSQL);
            stmt.execute(insertUsersSQL);
            stmt.execute(dropColumnSQL);
        }
    }

    private static int getRandomUKPRN(Random random) throws SQLException {
        String query = "SELECT UKPRN FROM institution ORDER BY RANDOM() LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt("UKPRN");
        }
        return 100000 + random.nextInt(900000);
    }
    

}