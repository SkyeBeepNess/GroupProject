package attendancePage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Student {

    private String studentID;
    private int attendanceStatus;
    private HashMap<Date, String> attendanceRecords;

    public Student(String studentID) {
        this.studentID = studentID;
        this.attendanceRecords = new HashMap<>();;
    }
    public double getAttendancePercentage() {
    	double attendanceNumber = 0.0;
    	
    	for (Map.Entry<Date, String> entry : getAttendanceRecords().entrySet()) {
    		Date key = entry.getKey();
    		String val = entry.getValue();
    		if (val.compareTo("Yes") == 0) {
				attendanceNumber += 1;
				
			}
    		else if (val.compareTo("Late") == 0) {
				attendanceNumber += 0.5;
			}
		}
    	return (attendanceNumber * 100)/getAttendanceRecords().size();
    }
    public void addAttendanceRecord(Date sessionDate, String attended) {
        attendanceRecords.put(sessionDate, attended);
    }
    public HashMap<Date, String> getAttendanceRecords() {
		return attendanceRecords;
	}
    
    public String getStudentID() { return studentID; }
    public int getAttendanceStatus() { return attendanceStatus; }
}
