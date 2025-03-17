package controllers;

import java.io.IOException;

import session.UserSession;
import dbhandlers.DataBaseManager;
import services.NavigationService;
import services.UIServices;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {
	@FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
	private void onLoginClicked() {
    	String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
        	UIServices.showAlert(AlertType.ERROR, "Login Error", "Empty field");
        	return;
        }

        DataBaseManager dbManager = DataBaseManager.getInstance();
        boolean isAuthenticated = dbManager.getUserByUsernameAndPassword(username, password);
        if (isAuthenticated) {
            UserSession session = UserSession.getInstance();
            if (session != null) {
                System.out.println("Logged in as: " + session.getUserId() + " " + session.getName() + " (" + session.getRole() + ")");
            }
            
            try {
            	FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HomePage.fxml"));
            	Parent root = loader.load();

            	Stage stage = (Stage) usernameField.getScene().getWindow(); 
                
                Scene scene = new Scene(root);
                stage.setScene(scene);
                
                stage.setTitle("University Management System - Home");
                
                stage.show();
                
            } catch (IOException e) {
            	e.printStackTrace();
            }
        } else {
            System.out.println("Incorrect login or password!");
            UIServices.showAlert(AlertType.ERROR, "Login Error", "Incorrect login or password");
        }
	}


	@FXML
    private void onRegisterClicked() {
    	try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registerPage.fxml"));
        	Parent root = loader.load();

        	Stage stage = (Stage) usernameField.getScene().getWindow(); 
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            stage.setTitle("University Management System - Registration");
            
            stage.show();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}
