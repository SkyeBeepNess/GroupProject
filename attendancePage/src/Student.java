package attendancePage;

public class Student {

    private String studentID;
    private String attendanceStatus;

    public Student(String studentID, String attendanceStatus) {
        this.studentID = studentID;
        this.attendanceStatus = attendanceStatus;
    }


    public String getStudentID() { return studentID; }
    public String getAttendanceStatus() { return attendanceStatus; }
}
