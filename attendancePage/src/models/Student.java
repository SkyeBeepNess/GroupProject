package models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private String studentID;
    private String name;
    private String courseID;
    private Map<LocalDate, String> attendanceRecords;

    public Student(String studentID, String courseID, String name) {
        this.studentID = studentID;
        this.setCourseID(courseID);
        this.setName(name);
        this.attendanceRecords = new HashMap<>();
    }


    public double getAttendancePercentage(LocalDate startDate, LocalDate endDate) {
        double attended = 0;
        int totalSessions = 0;

        for (Map.Entry<LocalDate, String> entry : attendanceRecords.entrySet()) {
            LocalDate sessionDate = entry.getKey();
            String status = entry.getValue();

            // ✅ If date range is null, count all records
            boolean withinRange = (startDate == null || !sessionDate.isBefore(startDate)) &&
                                  (endDate == null || !sessionDate.isAfter(endDate));

            if (withinRange) {
                if ("Yes".equals(status)) {
                    attended++;
                } else if ("Late".equals(status)) {
                    attended += 0.5;
                }
                totalSessions++;
            }
        }
        return (totalSessions == 0) ? 0.0 : (attended * 100) / totalSessions;
    }

    public void addAttendance(LocalDate date, String status) {
        attendanceRecords.put(date, status);
    }

    public String getStudentID() {
        return studentID;
    }

    public Map<LocalDate, String> getAttendanceRecords() {
        return attendanceRecords;
    }

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}
