package controllers;

import dbhandlers.ApplicantDAO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.io.ByteArrayInputStream;
import java.util.Base64;

import models.Applicant;
import models.Admin;
import session.UserSession;
import utils.ProfilePictureManager;
import services.NavigationService;
import services.UIServices;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ApplicantDetailsController {
	
	private ProfilePictureManager profileManager;
    
    private String passportPath, diplomaPath;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final Image exclamationImage = new Image(getClass().getResourceAsStream("/resources/img/exclamation.png"));
    private final Image checkImage = new Image(getClass().getResourceAsStream("/resources/img/check.png"));
    private final Image arrowDownImage = new Image(getClass().getResourceAsStream("/resources/img/arrow-down.png"));
    private final Image arrowRightImage = new Image(getClass().getResourceAsStream("/resources/img/arrow-right.png"));
    private final Image defaultProfile = new Image(getClass().getResourceAsStream("/resources/img/profpic.png"));
    private UserSession session = UserSession.getInstance();
    private ApplicantDAO applicantDAO;
    private Applicant currentApplicant;
    private boolean isAdminView;
    private boolean isDefault = false;
    

    @FXML private Text applicationIdField;
    @FXML private ImageView profileImageView;
    @FXML private VBox profileDropArea;
    @FXML private HBox profileImageHBox;
    @FXML private Button updateProfPicButton, deleteProfPicButton;
    @FXML private TextField firstNameField, lastNameField, nationalityField, certificateField, gradeField, institutionField;
    @FXML private Label passportDropArea, diplomaDropArea;
   // @FXML private TextField passportField, diplomaField;
    @FXML private DatePicker dobPicker, docPicker;
    @FXML private Button toggleButton1, toggleButton2, savePDButton, saveAQButton;
    @FXML private Button deletePassportButton, deleteDiplomaButton;
    @FXML private ImageView pdStatusImg, aqStatusImg, aqArrowImg, pdArrowImg;;
    @FXML private VBox personalDetailsVbox, academicalQualificationVbox;
    @FXML private VBox selectedPassportBox;
    @FXML private Hyperlink browseLinkPP;
    @FXML private Hyperlink browsePassportLink;
    @FXML private Hyperlink browseDiplomaLink;
    private ObjectProperty<File> selectedPassportFile = new SimpleObjectProperty<>(null);
    private ObjectProperty<File> selectedDiplomaFile = new SimpleObjectProperty<>(null);
    @FXML private Text passportFileName;
	@FXML private Text passportFileSize;
	@FXML private VBox selectedDiplomaBox;
	@FXML private Text diplomaFileName;
	@FXML private Text diplomaFileSize;
	@FXML private Button submitButton;


    @FXML
    private void initialize() {
    	applicantDAO = new ApplicantDAO();
    	//Object roleModel = UserSession.getInstance().getRoleModel();
        String role = session.getRole();
        String selectedApplicantId = session.getSelectedApplicantId();
    	isAdminView = "admin".equals(role) || "superadmin".equals(role);
    	/*
    	if (isAdminView) {
    		Admin admin = (Admin) roleModel;
    	}
    	*/
    	
    	selectedPassportFile.addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
            	passportDropArea.setVisible(false);
            	passportDropArea.setManaged(false);
                selectedPassportBox.setVisible(true);
                selectedPassportBox.setManaged(true);
                passportFileName.setText(newFile.getName());
                passportFileSize.setText("File size: " + newFile.length() / 1000 + "KB");
                if (isAdminView) {
    	        	deletePassportButton.setManaged(false);
    	        	deletePassportButton.setVisible(false);
    	        }
            } else {
                selectedPassportBox.setVisible(false);
                selectedPassportBox.setManaged(false);
                if (!isAdminView) {
                	passportDropArea.setVisible(true);
                	passportDropArea.setManaged(true);
                }
            }
        });
    	
    	selectedDiplomaFile.addListener((obs, oldFile, newFile) -> {
    	    if (newFile != null) {
    	        diplomaDropArea.setVisible(false);
    	        diplomaDropArea.setManaged(false);
    	        selectedDiplomaBox.setVisible(true);
    	        selectedDiplomaBox.setManaged(true);
    	        diplomaFileName.setText(newFile.getName());
    	        diplomaFileSize.setText("File size: " + newFile.length() / 1000 + "KB");
    	        if (isAdminView) {
    	        	deleteDiplomaButton.setManaged(false);
    	        	deleteDiplomaButton.setVisible(false);
    	        }
    	        
    	    } else {
    	        selectedDiplomaBox.setVisible(false);
    	        selectedDiplomaBox.setManaged(false);
    	        if (!isAdminView) {
    	        	diplomaDropArea.setVisible(true);
    	        	diplomaDropArea.setManaged(true);
    	        }
    	    }
    	});

        if (isAdminView) {
        	submitButton.setVisible(false);
        	submitButton.setManaged(false);
        	profileDropArea.setVisible(false);
        	profileDropArea.setManaged(false);
        	updateProfPicButton.setVisible(false);
    		updateProfPicButton.setManaged(false);
        	passportDropArea.setVisible(false);
            passportDropArea.setManaged(false);
            diplomaDropArea.setVisible(false);
	        diplomaDropArea.setManaged(false);
	       
            if (selectedApplicantId != null) {
                currentApplicant = applicantDAO.getApplicantByUserId(selectedApplicantId);
                //disableEditing();
                
            }
            
        } else if ("applicant".equals(role)) {
            currentApplicant = applicantDAO.getApplicantByUserId(session.getUserId());
            if (currentApplicant.getStatus() == "Submitted") {
            	disableEditing();
            }
        } else {
        	System.out.println("No active application");
        }
        
        profileManager = new ProfilePictureManager(
    		    currentApplicant,
    		    applicantDAO,
    		    profileImageView,
    		    profileDropArea,
    		    profileImageHBox,
    		    updateProfPicButton,
    		    deleteProfPicButton,
    		    isAdminView,
    		    defaultProfile
    		);
  
        
        if (currentApplicant != null) {
            fillFieldsWithApplicantData();
        }
        
        configureDatePicker(dobPicker);
        configureDatePicker(docPicker);
        updateStatusIcons();
              
    }
    

    private void fillFieldsWithApplicantData() {
    	applicationIdField.setText("Application ID: " + currentApplicant.getApplicationId());
        firstNameField.setText(currentApplicant.getName() != null ? currentApplicant.getName() : "");
        lastNameField.setText(currentApplicant.getLastName() != null ? currentApplicant.getLastName() : "");
        nationalityField.setText(currentApplicant.getNationality() != null ? currentApplicant.getNationality() : "");
        certificateField.setText(currentApplicant.getCertificate() != null ? currentApplicant.getCertificate() : "");
        gradeField.setText(currentApplicant.getGrade() != null ? currentApplicant.getGrade() : "");
        institutionField.setText(currentApplicant.getPreviousInstitution() != null ? currentApplicant.getPreviousInstitution() : "");

        if (currentApplicant.getDob() != null) {
            dobPicker.setValue(LocalDate.parse(currentApplicant.getDob(), dateFormatter));
        }
        if (currentApplicant.getDocDate() != null) {
            docPicker.setValue(LocalDate.parse(currentApplicant.getDocDate(), dateFormatter));
        }
        
        if (currentApplicant.getPassportPath() != null && !currentApplicant.getPassportPath().isEmpty()) {
            File file = new File(currentApplicant.getPassportPath());
            if (file.exists()) {
                selectedPassportFile.set(file);
                System.out.println("Passport file restored from path: " + file.getAbsolutePath());
            }
        }
        if (currentApplicant.getDiplomaPath() != null && !currentApplicant.getDiplomaPath().isEmpty()) {
            File file = new File(currentApplicant.getDiplomaPath());
            if (file.exists()) {
                selectedDiplomaFile.set(file);
                System.out.println("Diploma file restored from path: " + file.getAbsolutePath());
            }
        }
        
        if (currentApplicant.getProfilePicture() != null) {
        	System.out.println(currentApplicant.getProfilePicture());
            profileManager.loadFromBase64(currentApplicant.getProfilePicture());
        } else {
        	if (isAdminView) {
        		profileManager.updateUI(defaultProfile, true);
        	}
        	else {
            	profileImageHBox.setVisible(false);
            	profileImageHBox.setManaged(false);
        	}
        }
    }
    
    @FXML
    private void uploadPP() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Image");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
        	Image image = new Image(file.toURI().toString());
        	double width = image.getWidth();
            double height = image.getHeight();
            
            System.out.println("Width: " + width + ", Height: " + height);
            
            if (width > 300 || height > 500) {
            	UIServices.showAlert(AlertType.ERROR, "Invalid Image Dimensions", "Maximum allowed size is 300x500 pixels.");
            }
            else {
            	profileManager.setImageFromFile(file);
            }
        }
    }
    
    @FXML
    private void onDeletePPClicked() {
    	profileManager.clearImage();    		
    }
    
    @FXML
    private void handleDragOverPP(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }
    
    @FXML
    private void handleDropPP(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
        	File file = db.getFiles().get(0);
        	String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            	profileManager.setImageFromFile(file);
            } else {
                UIServices.showAlert(AlertType.ERROR, "Unsupported File Type", "Only JPG and PNG files are allowed.");
            }
        	
        }
        event.setDropCompleted(true);
        event.consume();
    }

    
    private void configureDatePicker(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(dateFormatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return parseDate(string);
            }
        });

        TextField editor = datePicker.getEditor();
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            String formatted = formatDateString(newValue, oldValue);
            if (!newValue.equals(formatted)) {
                editor.setText(formatted);
                editor.positionCaret(editor.getText().length());
            }
        });
    }

    private String formatDateString(String input, String oldValue) {
        String digits = input.replaceAll("[^\\d]", "");

        if (digits.length() > 8) {
            digits = digits.substring(0, 8);
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            formatted.append(digits.charAt(i));
            if ((i == 1 || i == 3) && i + 1 < digits.length()) {
                formatted.append("/");
            }
        }

        long oldSlashCount = oldValue.chars().filter(ch -> ch == '/').count();
        long newSlashCount = formatted.chars().filter(ch -> ch == '/').count();

        if (oldSlashCount > newSlashCount) {
            return oldValue;
        }

        return formatted.toString();
    }

    private LocalDate parseDate(String date) {
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return null;
        }

        try {
            String[] parts = date.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            int currentYear = LocalDate.now().getYear();

            if (month < 1) month = 1;
            if (month > 12) month = 12;

            int maxDays = YearMonth.of(year, month).lengthOfMonth();
            if (day < 1) day = 1;
            if (day > maxDays) day = maxDays;

            return LocalDate.of(year, month, day);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }
    @FXML
    private void disableEditing() {
        for (Node ch : personalDetailsVbox.getChildren()) {
            if (ch instanceof javafx.scene.control.TextInputControl) {
                TextInputControl input = (TextInputControl) ch;
                input.setEditable(false);
                input.setFocusTraversable(false);
            }
        }
    }
    
    @FXML
    private void onHomeClicked() {
    	NavigationService.navigateTo("HomePage.fxml", "Home");
    }

    public LocalDate getSelectedDate() {
        return dobPicker.getValue();
    }
    
    @FXML
    private void savePersonalDetails() {
        if (currentApplicant != null) {
            currentApplicant.setName(firstNameField.getText().trim());
            currentApplicant.setLastName(lastNameField.getText().trim());
            currentApplicant.setNationality(nationalityField.getText().trim());
            if (dobPicker.getValue() != null) {
                currentApplicant.setDob(dobPicker.getValue().format(dateFormatter));
            }
            applicantDAO.updatePersonalDetails(currentApplicant);
            if (selectedPassportFile.get() != null) {
            	applicantDAO.updatePassportPath(currentApplicant.getUserId(), selectedPassportFile.get().getAbsolutePath());
            }
            else {
            	applicantDAO.updatePassportPath(currentApplicant.getUserId(), null);
            }
            UIServices.showAlert(AlertType.INFORMATION, "Success", "Personal details saved successfully!");
        }
    }
    
    @FXML
    private void saveAcademicQualification() {
        if (currentApplicant != null) {
            currentApplicant.setCertificate(certificateField.getText().trim());
            currentApplicant.setInstitution(institutionField.getText().trim());
            currentApplicant.setGrade(gradeField.getText().trim());
            if (docPicker.getValue() != null) {
                currentApplicant.setDocDate(docPicker.getValue().format(dateFormatter));
            }
            applicantDAO.updateAcademicQualification(currentApplicant);
            if (selectedDiplomaFile.get() != null) {
            	applicantDAO.updateDiplomaPath(currentApplicant.getUserId(), selectedDiplomaFile.get().getAbsolutePath());
            }
            else {
            	applicantDAO.updateDiplomaPath(currentApplicant.getUserId(), null);
            }
            UIServices.showAlert(AlertType.INFORMATION, "Success", "Academic Qualification saved successfully!");
        }
    }

    @FXML
    private void personalDetailsPopup() {
        togglePopup(personalDetailsVbox, pdStatusImg, pdArrowImg);
    }

    @FXML
    private void academicQualificationPopup() {
        togglePopup(academicalQualificationVbox, aqStatusImg, aqArrowImg);
    }

    private void togglePopup(VBox vbox, ImageView statusImg, ImageView arrowImg) {
        boolean isVisible = !vbox.isVisible();
        vbox.setVisible(isVisible);
        vbox.setManaged(isVisible);
        arrowImg.setImage(isVisible ?  arrowRightImage : arrowDownImage);
        updateStatusIcons();
    }
    
    private void updateStatusIcons() {
        boolean personalComplete = !firstNameField.getText().trim().isEmpty() &&
                                   !nationalityField.getText().trim().isEmpty() &&
                                   dobPicker.getValue() != null && selectedPassportFile.get() != null;

        boolean academicComplete = !certificateField.getText().trim().isEmpty() &&
                                   !institutionField.getText().trim().isEmpty() &&
                                   docPicker.getValue() != null && selectedDiplomaFile.get() != null;

        pdStatusImg.setImage(personalComplete ? checkImage : exclamationImage);
        aqStatusImg.setImage(academicComplete ? checkImage : exclamationImage);
    }

    @FXML
    private void handleDragOverPassport(DragEvent event) {
        handleDragOver(event);
    }

    @FXML
    private void handleDropPassport(DragEvent event) {
        handleFileDrop(event, passportDropArea, true);
    }

    @FXML
    private void uploadPassport() {
        uploadFile(passportDropArea, true);
    }

    @FXML
    private void handleDragOverDiploma(DragEvent event) {
        handleDragOver(event);
    }

    @FXML
    private void handleDropDiploma(DragEvent event) {
        handleFileDrop(event, diplomaDropArea, false);
    }

    @FXML
    private void uploadDiploma() {
        uploadFile(diplomaDropArea, false);
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleFileDrop(DragEvent event, Label label, boolean isPassport) {     
        
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            if (db.getFiles().size() > 1) {
                UIServices.showAlert(AlertType.ERROR, "File Import Error", "Please select only one file");
            } else {
                File file = db.getFiles().get(0);
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".pdf")) {
                    if (isPassport) {
                        selectedPassportFile.set(file);
                    } else {
                        selectedDiplomaFile.set(file);
                    }
                } else {
                    UIServices.showAlert(AlertType.ERROR, "Unsupported File Type", "Only JPG, PNG, and PDF files are allowed.");
                }
            }
        }
        event.setDropCompleted(db.hasFiles());
        event.consume();
    }

    private void uploadFile(Label label, boolean isPassport) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF, Images", "*.pdf", "*.jpg", "*.png"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            if (isPassport) {
                selectedPassportFile.set(file);
            } else {
            	selectedDiplomaFile.set(file);
            }
        }
    }
    
    @FXML
    private void onPassportViewFileClicked() {
    	if (Desktop.isDesktopSupported()) {
            try {
            	if (selectedPassportFile.get() != null) {
            		Desktop.getDesktop().open(selectedPassportFile.get());
            	}
            	else if (currentApplicant.getPassportPath() != null) {
            		File passportFile = new File(currentApplicant.getPassportPath());
                    if (passportFile.exists()) {
                        Desktop.getDesktop().open(passportFile);
                    } else {
                        System.out.println("Passport file not found: " + passportFile.getAbsolutePath());
                    }
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop not supported");
        }
    }
    
    @FXML
    private void onDiplomaViewFileClicked() {
        if (Desktop.isDesktopSupported()) {
            try {
                if (selectedDiplomaFile.get() != null) {
                    Desktop.getDesktop().open(selectedDiplomaFile.get());
                } else if (currentApplicant.getDiplomaPath() != null) {
                    File file = new File(currentApplicant.getDiplomaPath());
                    if (file.exists()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        System.out.println("Diploma file not found: " + file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onDeletePassportClicked() {
    	selectedPassportFile.set(null);
    	
    }
    @FXML
    private void onDeleteDiplomaClicked() {
        selectedDiplomaFile.set(null);
    }
    
    @FXML
    private void onLogOutClicked() {
    	UserSession.clearSession();
    	selectedPassportFile.set(null);
    	selectedDiplomaFile.set(null);
    	NavigationService.navigateTo("loginPage.fxml", "Login");
    }
    
    @FXML
    private void onSubmitClicked() {
    	boolean profilePictureSet = currentApplicant.getProfilePicture() != null;
    	
    	boolean personalComplete = !firstNameField.getText().trim().isEmpty() &&
                !nationalityField.getText().trim().isEmpty() &&
                dobPicker.getValue() != null && selectedPassportFile.get() != null;

    	boolean academicComplete = !certificateField.getText().trim().isEmpty() &&
                !institutionField.getText().trim().isEmpty() &&
                docPicker.getValue() != null && selectedDiplomaFile.get() != null;
    	
    	if (profilePictureSet && personalComplete && academicComplete) {
    		saveAcademicQualification();
    		savePersonalDetails();
    		currentApplicant.setStatus("Submitted");
    		disableEditing();
    		UIServices.showAlert(AlertType.INFORMATION, "Success", "Application submitted successfully!");
    	}
    	else {
    		UIServices.showAlert(AlertType.ERROR, "Error", "Please fill all fields!");
    	}
    	
    }

}
