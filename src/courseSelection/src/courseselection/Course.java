package courseSelection.src.courseselection;

public class Course {
    private String courseId;
    private String courseName;
    private String duration;
    private String department;

    public Course(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }
    public Course(String courseId, String courseName, String duration, String department) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.duration = duration;
        this.department = department;
    }
    public String getId() {
        return courseId;
    }

    public String getName() {
        return courseName;
    }

    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getDuration() { return duration; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return courseId + " - " + courseName;
    }
}
