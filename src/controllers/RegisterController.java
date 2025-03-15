package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import services.UIServices;

import java.io.IOException;

import dbhandlers.DataBaseManager;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordConfirmField;

    private final DataBaseManager dbManager = DataBaseManager.getInstance();

    @FXML
    private void onRegisterClicked() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = passwordConfirmField.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        	UIServices.showAlert(AlertType.ERROR, "Registration Error", "Please fill all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
        	UIServices.showAlert(AlertType.ERROR, "Registration Error", "Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
        	UIServices.showAlert(AlertType.ERROR, "Registration Error", "Password must be at least 6 characters.");
            return;
        }

        if (dbManager.userExists(username)) {
        	UIServices.showAlert(AlertType.ERROR, "Registration Error", "Username already exists.");
            return;
        }

        String newUserId = dbManager.generateNewUserId();

        boolean success = dbManager.addUser(newUserId, name, username, password, "applicant");

        if (success) {
        	UIServices.showAlert(AlertType.INFORMATION, "Registration Successful", "You have been registered successfully!");
        	try {
            	
            	FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loginPage.fxml"));
            	Parent root = loader.load();

            	Stage stage = (Stage) usernameField.getScene().getWindow(); 
                
                Scene scene = new Scene(root);
                stage.setScene(scene);
                
                stage.setTitle("University Management System - Login");
                
                stage.show();
                
            } catch (IOException e) {
            	e.printStackTrace();
            }
        } else {
            UIServices.showAlert(AlertType.ERROR, "Registration Error", "Something went wrong. Please try again.");
        }
    }
    
    @FXML
    private void onLoginClicked() {
    	
    }
}
