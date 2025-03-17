package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.NavigationService;
import session.UserSession;

public class HomeController {
	@FXML
    private void initialize() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            System.out.println("Welcome, " + session.getName() + " (" + session.getRole() + ")");
        }	
	}
	
	@FXML
	private void onApplicantClicked() {
		
	}
	
	@FXML
	private void onAttendanceClicked() {
		 UserSession session = UserSession.getInstance();
		
		if ("admin".equals(session.getRole())) {
			//NavigationService.navigateTo("attendancePageAdmin.fxml", "Attendance - Admin");
		} else if ("student".equals(session.getRole())) {
			//NavigationService.navigateTo("attendancePageStudent.fxml", "Attendance - Admin");
		}
	}
}
