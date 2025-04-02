package courseSelection.src.courseselection;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

public class CourseDashboardController {

    @FXML private ListView<String> courseListView;
    @FXML private DatePicker examDatePicker;
    @FXML private DatePicker interviewDatePicker;
    @FXML private TextField bookingIdField;
    @FXML private TextField searchField;

    private List<Course> courseData;
    private CourseController controller = new CourseController();

    @FXML
    public void initialize() {
        courseData = controller.getCourses();

        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Course c : courseData) {
            displayList.add(c.toString());
        }
        courseListView.setItems(displayList);
    }

    @FXML
    private void bookAppointment() {
        int selectedIndex = courseListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Missing Course", "Please select a course.");
            return;
        }

        LocalDate examDate = examDatePicker.getValue();
        LocalDate interviewDate = interviewDatePicker.getValue();

        if (examDate == null || interviewDate == null) {
            showAlert("Missing Dates", "Please select both exam and interview dates.");
            return;
        }

        if (!examDate.isBefore(interviewDate)) {
            showAlert("Invalid Dates", "Exam date must be before interview date.");
            return;
        }

        Course selectedCourse = courseData.get(selectedIndex);
        String userEmail = "student@example.com"; // Replace with dynamic user email if available

        String bookingId = controller.bookAppointment(selectedCourse, examDate, interviewDate, userEmail);
        bookingIdField.setText(bookingId); // Show booking ID to the student
        showAlert("Booking Confirmed", "Your booking ID is: " + bookingId);
    }

    @FXML
    private void updateBooking() {
        System.out.println("Update button clicked");
        // Future logic for updating booking
    }

    @FXML
    private void cancelBooking() {
        System.out.println("Cancel button clicked");
        // Future logic for canceling booking
    }

    @FXML
    private void searchCourses() {
        String query = searchField.getText().toLowerCase();
        ObservableList<String> filtered = FXCollections.observableArrayList();
        for (Course c : courseData) {
            if (c.toString().toLowerCase().contains(query)) {
                filtered.add(c.toString());
            }
        }
        courseListView.setItems(filtered);
    }

    @FXML
    private void viewBooking() {
        String bookingId = bookingIdField.getText().trim();
        if (bookingId.isEmpty()) {
            showAlert("Error", "Please enter your Booking ID to view your appointment.");
            return;
        }

        Appointment appointment = controller.findAppointment(bookingId);
        if (appointment != null) {
            showAlert("My Booking", appointment.toString());
        } else {
            showAlert("Not Found", "No booking found for ID: " + bookingId);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 