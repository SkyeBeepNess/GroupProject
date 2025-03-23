package controllers;

import java.util.List;

import dbhandlers.ApplicantDAO;
import dbhandlers.DataBaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.Admin;
import models.Applicant;
import services.NavigationService;
import services.UIServices;
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
		UserSession session = UserSession.getInstance();
		
		if ("applicant".equals(session.getRole())) {
			ApplicantDAO applicantDAO = new ApplicantDAO();
			Applicant applicantModel = applicantDAO.getApplicantByUserId(session.getUserId());
			UserSession.getInstance().setRoleModel(applicantModel);
			NavigationService.navigateTo("ApplicantDetails.fxml", "ApplicantDetails");
		}
		else if ("admin".equals(session.getRole()) || "superadmin".equals(session.getRole())){
			/*
			List<String> ukprns = DataBaseManager.getAdminUKPRNbyUserID(session.getUserId(), session.getRole());
        	Admin adminModel = new Admin(session.getUserId(), ukprns);
            UserSession.getInstance().setRoleModel(adminModel);
            */
			NavigationService.navigateTo("ApplicantListAdmin.fxml", "Applicant List");
		}
		else {
			UIServices.showAlert(AlertType.ERROR, "Access Denied", "Please contact support");
		}
	}
	
	@FXML
	private void onCoursesClicked() { UIServices.showWorkings(); }
	@FXML
	private void onFinancesClicked() { UIServices.showWorkings(); }
	@FXML
	private void onRestaurantClicked() { UIServices.showWorkings(); }
	@FXML
	private void onSportClicked() { UIServices.showWorkings(); }
	
	@FXML
	private void onAttendanceClicked() {
		 UserSession session = UserSession.getInstance();
		
		if ("admin".equals(session.getRole()) || "superadmin".equals(session.getRole())) {
			NavigationService.navigateTo("attendancePageAdmin.fxml", "Attendance - Admin");
		} else if ("student".equals(session.getRole())) {
			NavigationService.navigateTo("attendancePageStudent.fxml", "Attendance - Student");
		}
		else {
			UIServices.showAlert(AlertType.ERROR, "Access Denied", "You are not a student yet");
		}
	}
	
	@FXML
    private void onLogOutClicked() {
    	UserSession.clearSession();
    	NavigationService.navigateTo("loginPage.fxml", "Login");		
	}
}
