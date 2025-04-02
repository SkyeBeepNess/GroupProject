package courseSelection.src.courseselection;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class DashboardController {
    @FXML private ListView<Course> courseListView;
    @FXML private DatePicker examDatePicker, interviewDatePicker;
    @FXML private TextField emailField;
    private CourseController controller = new CourseController();

    @FXML
    public void initialize() {
        courseListView.getItems().setAll(controller.getCourses());
    }

    @FXML
    private void bookAppointment() {
        Course selected = courseListView.getSelectionModel().getSelectedItem();
        LocalDate examDate = examDatePicker.getValue();
        LocalDate interviewDate = interviewDatePicker.getValue();
        String email = emailField.getText();

        if(selected == null || examDate == null || interviewDate == null || email.isEmpty()) {
            showAlert("Error", "Fill all fields!");
            return;
        }

        String bookingId = controller.bookAppointment(selected, examDate, interviewDate, email);
        showAlert("Success", "Booking ID: " + bookingId);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
