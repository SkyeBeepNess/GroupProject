package attendancePage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {

    private String studentID;
    private int attendanceStatus;
    private HashMap<LocalDate, String> attendanceRecords;
	private static final String URL = "jdbc:sqlite:resources/UNIMAN.db";


    
    
    
    public Student(String studentID) {
    	this.attendanceRecords = DataBaseHelper.fetchAllAttendanceRecordsByStudent(studentID);
    	this.studentID = studentID;
    	
    }

    
    
    public double getAttendancePercentageByDate(List<LocalDate> dateRange) {
        double attendanceNumber = 0.0;
        int classesAmount = 0;

        // Ensure dateRange has at least two dates
        LocalDate startDate = (dateRange != null && dateRange.size() > 1) ? dateRange.get(0) : null;
        LocalDate endDate = (dateRange != null && dateRange.size() > 1) ? dateRange.get(1) : null;

        for (Map.Entry<LocalDate, String> entry : getAttendanceRecords().entrySet()) {
            LocalDate sessionDate = entry.getKey();
            String attendanceStatus = entry.getValue();

            // If date range is provided, check if the session date is within range
            boolean withinRange = (startDate == null || (sessionDate.isAfter(startDate) && sessionDate.isBefore(endDate)));

            if (withinRange) {
                if ("Yes".equals(attendanceStatus)) {
                    attendanceNumber += 1;
                } else if ("Late".equals(attendanceStatus)) {
                    attendanceNumber += 0.5;
                }
                classesAmount++;
            }
        }

        return (classesAmount == 0) ? 0.0 : (attendanceNumber * 100) / classesAmount;
    }

    
    

    
    
    
    public HashMap<LocalDate, String> getAttendanceRecordsByDate(List<LocalDate> dateRange) {
    	HashMap<LocalDate, String> attendanceRecordsByDate = new HashMap<LocalDate, String>();
    	for (Map.Entry<LocalDate, String> entry : attendanceRecords.entrySet()) {
			LocalDate key = entry.getKey();
			String val = entry.getValue();
			if (key.isAfter(dateRange.get(0)) && key.isBefore(dateRange.get(1))) {
				attendanceRecordsByDate.put(key, val);
			}
		}
		return attendanceRecordsByDate;
	
		
	
	}
    

    
    
    
    
    public String getStudentID() { return studentID; }
    public int getAttendanceStatus() { return attendanceStatus; }
    public HashMap<LocalDate, String> getAttendanceRecords() { return attendanceRecords; }
    
	public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
