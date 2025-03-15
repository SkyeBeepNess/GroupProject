package dbhandlers;

public class DataBaseCreation {
	private String applicantColumnsUpdate = "ALTER TABLE applicants ADD COLUMN UKPRN INTEGER; "
			+ "ALTER TABLE applicants ADD COLUMN ApplicationID TEXT; "
			+ "UPDATE applicants "
			+ "SET UKPRN = (SELECT UKPRN FROM institution ORDER BY RANDOM() LIMIT 1) "
			+ "WHERE UKPRN IS NULL; "
			+ "UPDATE applicants "
			+ "SET ApplicationID = CAST(UKPRN AS TEXT) || CAST(ABS(RANDOM()) % 900000 + 100000 AS TEXT) "
			+ "WHERE ApplicationID IS NULL; "
			+ "ALTER TABLE applicants ADD COLUMN Status TEXT DEFAULT 'Pending'; "
			+ "UPDATE applicants "
			+ "SET Status = 'Pending' "
			+ "WHERE Status IS NULL;";
	
	private String applicantUserIDInitialize = "ALTER TABLE applicants ADD COLUMN UserID TEXT; "
			+ "WITH NumberedApplicants AS ("
			+ "    SELECT rowid, "
			+ "           printf('%012d', 5 + ROW_NUMBER() OVER (ORDER BY rowid)) AS GeneratedUserID "
			+ "    FROM applicants"
			+ ") "
			+ "UPDATE applicants "
			+ "SET UserID = (SELECT GeneratedUserID FROM NumberedApplicants WHERE applicants.rowid = NumberedApplicants.rowid);";
	
	private String usernamePasswordCreation = "ALTER TABLE applicants ADD COLUMN username_temp TEXT; "
			+ "WITH NumberedUsers AS ( "
			+ "    SELECT "
			+ "        UserID, "
			+ "        \"Applicant Name\", "
			+ "        LOWER(REPLACE(\"Applicant Name\", ' ', '_')) || '_' || "
			+ "        ROW_NUMBER() OVER (PARTITION BY \"Applicant Name\" ORDER BY UserID) AS GeneratedUsername "
			+ "    FROM applicants "
			+ ") "
			+ "UPDATE applicants "
			+ "SET username_temp = (SELECT GeneratedUsername FROM NumberedUsers WHERE applicants.UserID = NumberedUsers.UserID); "
			+ "INSERT INTO users (UserID, Name, Role, username, password) "
			+ "SELECT "
			+ "    UserID, "
			+ "    \"Applicant Name\", "
			+ "    'applicant' AS Role, "
			+ "    username_temp, "
			+ "    SUBSTR(UserID, -6) AS password "
			+ "FROM applicants "
			+ "WHERE NOT EXISTS ("
			+ "    SELECT 1 FROM users u WHERE u.UserID = applicants.UserID "
			+ ")"
			+ "ALTER TABLE applicants DROP COLUMN username_temp;";
}
