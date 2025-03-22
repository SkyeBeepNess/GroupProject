package controllers;

import java.awt.Desktop;
import java.io.File;

import dbhandlers.ApplicantDAO;
import dbhandlers.DataBaseManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Applicant;
import services.NavigationService;
import services.UIServices;
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
    @FXML private Button rejectButton;
    @FXML private Button acceptButton;
    
    private final ApplicantDAO applicantDAO = new ApplicantDAO();
    private final ObservableList<Applicant> applicants = FXCollections.observableArrayList();
    private String csvFilePath;
    @FXML private StackPane fileUploadOverlay;
    @FXML private VBox selectedFileBox;
    @FXML private Hyperlink browseLink;
    private ObjectProperty<File> selectedFile = new SimpleObjectProperty<>(null);
    @FXML private Text fileName;
	@FXML private Text fileSize;
	@FXML private Button submitFileButton;
	

    @FXML
    private void initialize() {
        loadApplicants();
    	
    	viewDetailsButton.setDisable(true);
    	rejectButton.setDisable(true);
    	acceptButton.setDisable(true);
    	submitFileButton.setDisable(true);

        applicantTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewDetailsButton.setDisable(newSelection == null);
            acceptButton.setDisable(newSelection == null);
            rejectButton.setDisable(newSelection == null);
        });
        
        //FILE
        selectedFile.addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
                handleFileSelected(newFile);
                submitFileButton.setDisable(false);
            } else {
                selectedFileBox.setVisible(false);
                submitFileButton.setDisable(true);
            }
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
    private void openImportCSVOverlay() {
    	selectedFile.set(null);
		fileUploadOverlay.setVisible(true);
    	
	}
    @FXML
    private void cancelCSVImport() {
    	selectedFile.set(null);
    	fileUploadOverlay.setVisible(false);
    	selectedFileBox.setVisible(false);
	}
   
    @FXML
    private void handleBrowseLink() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File(s)");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx")
        );

        Window window = browseLink.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            selectedFile.set(file);
        }
    }

    
    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }
    
    @FXML
    private void handleDragDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            if (db.getFiles().size() > 1) {
                UIServices.showAlert(AlertType.ERROR, "File Import Error", "Please select only one file");
            } else {
                File file = db.getFiles().get(0);
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".csv") || fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    selectedFile.set(file);
                } else {
                    System.out.println("Unsupported file format: " + fileName);
                }
            }
        }

        event.setDropCompleted(db.hasFiles());
        event.consume();
    }

    @FXML
    private void handleFileSelected(File file) {
        selectedFileBox.setVisible(true);
        fileName.setText(file.getName());
        fileSize.setText("File size: " + file.length() / 1000 + "KB");
    }

    @FXML
    private void handleSubmitFiles() {
        File file = selectedFile.get();
        if (file == null) {
            UIServices.showAlert(AlertType.ERROR, "File import error", "Please choose a file to upload");
            return;
        }

        DataBaseManager.loadApplicantsFromCSV(selectedFile.get().getAbsolutePath());
        cancelCSVImport();
    }

    
    @FXML
    private void onViewFileClicked() {
    	if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(selectedFile.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop not supported");
        }
    }
    
    @FXML
    private void onDeleteFileClicked() {
    	selectedFile.set(null);
    	selectedFileBox.setVisible(false);
    	
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
