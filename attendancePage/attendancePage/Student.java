package attendancePage;

public class Student {

    private String studentID;
    private int attendanceStatus;

    public Student(String studentID) {
        this.studentID = studentID;
        this.attendanceStatus = attendancePercentage();
    }
    private int attendancePercentage() {
    	return 100;
    }

    public String getStudentID() { return studentID; }
    public int getAttendanceStatus() { return attendanceStatus; }
}
