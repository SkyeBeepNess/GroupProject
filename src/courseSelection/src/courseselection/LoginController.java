package courseSelection.src.courseselection;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Simple hardcoded validation
        if ((username.equals("admin") && password.equals("admin123")) ||
            (username.equals("student") && password.equals("student123"))) {
            openMainApp(); // success
        } else {
            showError("Invalid username or password. Please try again.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText("Login Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openMainApp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/courseselection/CourseBookingDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("UMS - Course Booking Dashboard");
            stage.setScene(new Scene(root));
            stage.show(); // ✅ Don’t forget this
        } catch (IOException e) {
            showError("Failed to load the dashboard:\n" + e.getMessage());
            e.printStackTrace();
        }
    }



}
