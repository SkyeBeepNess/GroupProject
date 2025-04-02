package courseSelection.src.courseselection;

import java.time.LocalDate;
import java.util.*;

public class CourseController {
    private List<Course> courses;
    private Map<String, Appointment> bookings;

    public CourseController() {
        courses = CSVHandler.loadCourses("C:\\Users\\shaje\\Downloads\\UMS Data\\UMS Data\\KISCOURSE.csv");
        bookings = BookingHandler.loadBookings("C:\\Users\\shaje\\Downloads\\UMS Data\\UMS Data\\bookings.csv");
    }

    public List<Course> getCourses() { return courses; }

    public String bookAppointment(Course course, LocalDate examDate, LocalDate interviewDate, String userEmail) {
        String bookingId = "BK" + System.currentTimeMillis();
        bookings.put(bookingId, new Appointment(bookingId, course, examDate, interviewDate));
        BookingHandler.saveBookings(bookings, "C:/Users/shaje/Downloads/UMS Data/bookings.csv");

        // Send confirmation email clearly
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.sendConfirmationEmail(userEmail, bookingId);

        return bookingId;
    }

    

    public Appointment findAppointment(String bookingId) {
        return bookings.get(bookingId);
    }

    public boolean cancelBooking(String bookingId) {
        if (bookings.remove(bookingId) != null) {
            BookingHandler.saveBookings(bookings, "C:\\Users\\shaje\\Downloads\\UMS Data\\UMS Data\\bookings.csv");
            return true;
        }
        return false;
    }
}
