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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;
import models.Applicant;
import session.UserSession;
import services.NavigationService;
import services.UIServices;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApplicantDetailsController {
    
    private String passportPath, diplomaPath;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final Image exclamationImage = new Image(getClass().getResourceAsStream("/resources/img/exclamation.png"));
    private final Image checkImage = new Image(getClass().getResourceAsStream("/resources/img/check.png"));
    private final Image arrowDownImage = new Image(getClass().getResourceAsStream("/resources/img/arrow-down.png"));
    private final Image arrowRightImage = new Image(getClass().getResourceAsStream("/resources/img/arrow-right.png"));

    private ApplicantDAO applicantDAO;
    private Applicant currentApplicant;
    private boolean isAdminView;

    @FXML private TextField firstNameField, lastNameField, nationalityField, certificateField, gradeField, institutionField;
    @FXML private Label passportDropArea, diplomaDropArea;
    @FXML private TextField passportField, diplomaField;
    @FXML private DatePicker dobPicker, docPicker;
    @FXML private Button toggleButton1, toggleButton2, savePDButton, saveAQButton;
    @FXML private ImageView pdStatusImg, aqStatusImg, aqArrowImg, pdArrowImg;;
    @FXML private VBox personalDetailsVbox, academicalQualificationVbox;
    @FXML private VBox selectedPassportBox;
    @FXML private Hyperlink browseLink, browsePassportLink;
    private ObjectProperty<File> selectedPassportFile = new SimpleObjectProperty<>(null);
    @FXML private Text fileName;
	@FXML private Text fileSize;

    @FXML
    private void initialize() {
    	UserSession session = UserSession.getInstance();
    	applicantDAO = new ApplicantDAO();
        String role = session.getRole();
        String selectedApplicantId = session.getSelectedApplicantId();

        isAdminView = "admin".equals(role);

        if (isAdminView) {
            if (selectedApplicantId != null) {
                currentApplicant = applicantDAO.getApplicantByUserId(selectedApplicantId);
                //disableEditing();
                
                savePDButton.setVisible(false);
                saveAQButton.setVisible(false);
            }
        } else {
            currentApplicant = applicantDAO.getApplicantByUserId(session.getUserId());
        }

        if (currentApplicant != null) {
            fillFieldsWithApplicantData();
        }
        configureDatePicker(dobPicker);
        configureDatePicker(docPicker);
        updateStatusIcons();
        
        selectedPassportFile.addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
            	passportDropArea.setVisible(false);
                selectedPassportBox.setVisible(true);
                fileName.setText(newFile.getName());
                fileSize.setText("File size: " + newFile.length() / 1000 + "KB");
            } else {
                selectedPassportBox.setVisible(false);
                passportDropArea.setVisible(true);
            }
        });
        
    }
    

    private void fillFieldsWithApplicantData() {
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
            UIServices.showAlert(AlertType.INFORMATION, "Success", "Academic Qualificaation saved successfully!");
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
                                   dobPicker.getValue() != null;

        boolean academicComplete = !certificateField.getText().trim().isEmpty() &&
                                   !institutionField.getText().trim().isEmpty() &&
                                   docPicker.getValue() != null;

        pdStatusImg.setImage(personalComplete ? checkImage : exclamationImage);
        aqStatusImg.setImage(academicComplete ? checkImage : exclamationImage);
    }

    @FXML
    private void handleDragOverPassport(DragEvent event) {
        handleDragOver(event);
    }

    @FXML
    private void handleDropPassport(DragEvent event) {
        handleFileDrop(event, passportField, passportDropArea, true);
    }

    @FXML
    private void uploadPassport() {
        uploadFile(passportField, passportDropArea, true);
    }

    @FXML
    private void handleDragOverDiploma(DragEvent event) {
        handleDragOver(event);
    }

    @FXML
    private void handleDropDiploma(DragEvent event) {
        handleFileDrop(event, diplomaField, diplomaDropArea, false);
    }

    @FXML
    private void uploadDiploma() {
        uploadFile(diplomaField, diplomaDropArea, false);
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleFileDrop(DragEvent event, TextField textField, Label label, boolean isPassport) {     
        
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            if (db.getFiles().size() > 1) {
                UIServices.showAlert(AlertType.ERROR, "File Import Error", "Please select only one file");
            } else {
                File file = db.getFiles().get(0);
                if (isPassport) {
                    passportPath = file.getAbsolutePath();
                    applicantDAO.updatePassportPath(currentApplicant.getUserId(), passportPath);
                    selectedPassportFile.set(file);
                } else {
                    diplomaPath = file.getAbsolutePath();
                    applicantDAO.updateDiplomaPath(currentApplicant.getUserId(), diplomaPath);
                }
            }
        }
        event.setDropCompleted(db.hasFiles());
        event.consume();
    }

    private void uploadFile(TextField textField, Label label, boolean isPassport) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF, Images", "*.pdf", "*.jpg", "*.png"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            if (isPassport) {
                passportPath = file.getAbsolutePath();
                applicantDAO.updatePassportPath(currentApplicant.getUserId(), passportPath);
                selectedPassportFile.set(file);
            } else {
                diplomaPath = file.getAbsolutePath();
                applicantDAO.updateDiplomaPath(currentApplicant.getUserId(), diplomaPath);
            }
        }
    }
    
    @FXML
    private void onPassportViewFileClicked() {
    	if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(selectedPassportFile.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop not supported");
        }
    }
    
    @FXML
    private void onDeletePassportClicked() {
    	selectedPassportFile.set(null);
    	selectedPassportBox.setVisible(false);
    	
    }
    
}
