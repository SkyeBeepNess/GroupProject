package courseSelection.src.courseselection;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import java.time.LocalDate;

public class CourseBookingController {

    @FXML private ListView<String> courseListView;
    @FXML private DatePicker examDatePicker;
    @FXML private DatePicker interviewDatePicker;
    @FXML private TextField bookingIdField;

    private final CourseController controller = new CourseController();

    @FXML
    public void initialize() {
        // Load courses into ListView
        courseListView.setId(FXCollections.observableArrayList(controller.getCourses()));
    }
    @FXML
    private void bookAppointment() {
        String selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        LocalDate examDate = examDatePicker.getValue();
        LocalDate interviewDate = interviewDatePicker.getValue();

        if (selectedCourse == null || examDate == null || interviewDate == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (!examDate.isBefore(interviewDate) || examDate.isBefore(LocalDate.now())) {
            showAlert("Error", "Exam date must be before interview date and both in future.");
            return;
        }

        String bookingId = controller.bookAppointment(selectedCourse, examDate, interviewDate);
        showAlert("Booking Confirmed", "Your booking ID is: " + bookingId);
    }

    @FXML
    private void searchBooking() {
        String bookingId = bookingIdField.getText();
        Appointment result = controller.findAppointment(bookingId);
        showAlert("Search Result", result != null ? result : "No booking found.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

