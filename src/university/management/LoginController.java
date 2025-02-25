package university.management;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.net.URL;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("admin") && password.equals("admin123")) {
            loadScene(event, "admin_view.fxml", "Admin Dashboard", null);
        } else if (username.startsWith("applicant") && password.equals("applicant123")) {
            loadScene(event, "applicant_view.fxml", "Applicant Dashboard", username);
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private void loadScene(ActionEvent event, String fxmlFile, String title, String applicantName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (applicantName != null) {
                ApplicantController controller = loader.getController();
                controller.setApplicantName(applicantName);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlFile);
        }
    }
}
