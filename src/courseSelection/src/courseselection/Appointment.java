package courseSelection.src.courseselection;

import java.time.LocalDate;

public class Appointment {
    private String bookingId;
    private Course course;
    private LocalDate examDate;
    private LocalDate interviewDate;

    public Appointment(String bookingId, Course course, LocalDate examDate, LocalDate interviewDate) {
        this.bookingId = bookingId;
        this.course = course;
        this.examDate = examDate;
        this.interviewDate = interviewDate;
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public Course getCourse() { return course; }
    public LocalDate getExamDate() { return examDate; }
    public LocalDate getInterviewDate() { return interviewDate; }

    // Setters
    public void setExamDate(LocalDate date) { this.examDate = date; }
    public void setInterviewDate(LocalDate date) { this.interviewDate = date; }

    // ToString for display
    @Override
    public String toString() {
        return "📌 Booking ID: " + bookingId +
               "\n📘 Course: " + course.getCourseName() +
               "\n📎 Course ID: " + course.getCourseId() +
               "\n📝 Exam Date: " + examDate +
               "\n🗓️ Interview Date: " + interviewDate;
    }
}
