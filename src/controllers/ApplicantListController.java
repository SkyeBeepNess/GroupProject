package controllers;

import dbhandlers.ApplicantDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Applicant;
import services.NavigationService;
import session.UserSession;

public class ApplicantListController {

    @FXML private TableView<Applicant> applicantTable;
    @FXML private TableColumn<Applicant, String> colApplicantName;
    @FXML private TableColumn<Applicant, String> colDate;
    @FXML private TableColumn<Applicant, String> colCertificate;
    @FXML private TableColumn<Applicant, String> colGrade;
    @FXML private TableColumn<Applicant, String> colApplicationId;
    @FXML private TableColumn<Applicant, String> colStatus;
    @FXML private Button viewDetailsButton;
    
    private final ApplicantDAO applicantDAO = new ApplicantDAO();
    private final ObservableList<Applicant> applicants = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadApplicants();
    	
    	viewDetailsButton.setDisable(true);

        applicantTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewDetailsButton.setDisable(newSelection == null);
        });
    }

    private void loadApplicants() {
        applicants.clear();
        applicants.addAll(applicantDAO.getAllApplicants());

        colApplicantName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateOfApplication"));
        colCertificate.setCellValueFactory(new PropertyValueFactory<>("certificate"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colApplicationId.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        applicantTable.setItems(applicants);
    }

    
    
    @FXML
    private void onHomeClicked() {
    	NavigationService.navigateTo("HomePage.fxml", "Home");
    }
    
    @FXML
    private void onImportClicked() {
    }
    
    @FXML
    private void onFilterClicked() {
    }
    
    @FXML
    private void onSearchClicked() {
    }
    
    @FXML
    private void onRejectClicked() {}
    
    @FXML
    private void viewApplicantDetails() {
    	Applicant selectedApplicant = applicantTable.getSelectionModel().getSelectedItem();
        if (selectedApplicant != null) {
            UserSession.setSelectedApplicantId(selectedApplicant.getUserId());

            NavigationService.navigateTo("applicantDetails.fxml", "Applicant Details");
        }
    }
    
    @FXML
    private void onAcceptClicked() {}
}
