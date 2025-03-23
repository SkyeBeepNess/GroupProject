package controllers;

import java.awt.Desktop;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Admin;
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
    @FXML private TableColumn<Applicant, String> colUKPRN;
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
	@FXML private ScrollPane ukprnFilter;
	@FXML private StackPane filterOverlay;
	@FXML private DatePicker startDate;
	@FXML private DatePicker endDate;
	@FXML private TextField searchTextField;
	String searchInput;
	
	private Admin admin;
	private GridPane ukprnGrid = new GridPane();
	private ArrayList<CheckBox> checkboxes = new ArrayList<>();
	private List<String> ukprns;
	private List<String> toFilterUKPRN;
	String stDate, enDate;

    @FXML
    private void initialize() {
    	//admin = (Admin) UserSession.getInstance().getRoleModel();
    	ukprns = DataBaseManager.getAdminUKPRNbyUserID(UserSession.getInstance().getUserId(), UserSession.getInstance().getRole());
    	admin = new Admin(UserSession.getInstance().getUserId(), ukprns);
    	filterOverlay.setVisible(false);
    	
        loadApplicants(null, null, ukprns, null);
        configureFilterGrid();
    	
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
    
    private void configureFilterGrid() {
    	List<String> ukprns = admin.getManagedUKPRN(); 
    	ukprnGrid.setHgap(15);
    	ukprnGrid.setVgap(0);
    	for (int i = 0; i < ukprns.size(); i++) {
    		CheckBox ukprnCheckBox = new CheckBox();
    		ukprnCheckBox.setText(ukprns.get(i));
    		ukprnCheckBox.setId(ukprns.get(i));
    		checkboxes.add(ukprnCheckBox);
    		GridPane.setColumnIndex(ukprnCheckBox, i % 6);
            GridPane.setRowIndex(ukprnCheckBox, i / 6);
		}
    	ukprnGrid.getChildren().addAll(checkboxes);
    	ukprnFilter.setContent(ukprnGrid);
    }

    private void loadApplicants(String stDate, String enDate, List<String> ukprnFiltered, String search) {
        applicants.clear();
        applicants.addAll(applicantDAO.getApplicantsBy(stDate, enDate, ukprnFiltered, search));

        colApplicantName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateOfApplication"));
        colCertificate.setCellValueFactory(new PropertyValueFactory<>("certificate"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colApplicationId.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        colUKPRN.setCellValueFactory(new PropertyValueFactory<>("ukprn"));
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

        boolean success = DataBaseManager.loadApplicantsFromCSV(selectedFile.get().getAbsolutePath());
        if (success) {
        	cancelCSVImport();
        	toFilterUKPRN = toFilterUKPRN == null || toFilterUKPRN.isEmpty() ? ukprns : toFilterUKPRN;
            loadApplicants(stDate, enDate, toFilterUKPRN, searchInput);
        }
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
    	boolean isVisible = !filterOverlay.isVisible();
    	filterOverlay.setVisible(isVisible);
    }
    
    @FXML
    private void onCloseFilterClicked() {
    	filterOverlay.setVisible(false);
    }
    
    private List<String> getSelectedUkprns() {
        List<String> selected = new ArrayList<>();
        for (CheckBox cb : checkboxes) {
            if (cb.isSelected()) {
                selected.add(cb.getText());
            }
        }
        return selected != null ? selected : ukprns;
    }
        
    @FXML
    private void handleApplyFilters() {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	stDate = startDate.getValue() != null ? startDate.getValue().format(formatter) : null;
    	enDate = endDate.getValue() != null ? endDate.getValue().format(formatter) : null;
    	System.out.println(stDate + " - " + enDate);
    	toFilterUKPRN = getSelectedUkprns();
    	System.out.println(toFilterUKPRN);
    	System.out.println(searchInput);
    	toFilterUKPRN = toFilterUKPRN == null || toFilterUKPRN.isEmpty() ? ukprns : toFilterUKPRN;
    	loadApplicants(stDate, enDate, toFilterUKPRN, searchInput);
    	filterOverlay.setVisible(false);    	
    }
    
    @FXML
    private void onSearchClicked() {
    	searchInput = searchTextField.getText().trim();
    	System.out.println(searchInput);
        loadApplicants(stDate, enDate, toFilterUKPRN, searchInput);
    }
    
    @FXML
    private void onRejectClicked() {
    	Applicant selectedApplicant = applicantTable.getSelectionModel().getSelectedItem();
    	if (selectedApplicant != null) {
            boolean success = applicantDAO.updateApplicantStatus(selectedApplicant.getUserId(), "Rejected");
            if (success) {
                selectedApplicant.setStatus("Rejected");
                applicantTable.refresh();
            }
        }
            
    }
    
    @FXML
    private void viewApplicantDetails() {
    	Applicant selectedApplicant = applicantTable.getSelectionModel().getSelectedItem();
        if (selectedApplicant != null) {
            UserSession.setSelectedApplicantId(selectedApplicant.getUserId());

            NavigationService.navigateTo("applicantDetails.fxml", "Applicant Details");
        }
    }
    
    @FXML
    private void onAcceptClicked() {
    	Applicant selectedApplicant = applicantTable.getSelectionModel().getSelectedItem();
    	boolean success = applicantDAO.updateApplicantStatus(selectedApplicant.getUserId(), "Accepted");
        if (success) {
            selectedApplicant.setStatus("Accepted");
            applicantTable.refresh();
        }
    }
    
    @FXML
    private void onLogOutClicked() {
    	UserSession.clearSession();
    	ukprns.clear();
    	startDate = null;
    	endDate = null;
    	toFilterUKPRN = null;
    	NavigationService.navigateTo("loginPage.fxml", "Login");
    }
    
}
