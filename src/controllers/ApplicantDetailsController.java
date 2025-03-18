package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import services.NavigationService;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApplicantDetailsController {
    private String passportPath, diplomaPath;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean isPopup1Visible = false;
    private boolean isPopup2Visible = false;
    private final Image exclamationImage = new Image(getClass().getResourceAsStream("/views/img/exclamation.png"));
    private final Image checkImage = new Image(getClass().getResourceAsStream("/views/img/check.png"));

    @FXML private Label passportDropArea, diplomaDropArea;
    @FXML private TextField passportField, diplomaField;
    @FXML private DatePicker dobPicker, docPicker;
    @FXML private Button toggleButton1, toggleButton2;
    @FXML private ImageView pdStatusImg, aqStatusImg;
    @FXML private VBox personalDetailsVbox, academicalQualificationVbox;

    @FXML
    private void initialize() {
        configureDatePicker(dobPicker);
        configureDatePicker(docPicker);
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
        if (digits.length() > 8) digits = digits.substring(0, 8);
        
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            formatted.append(digits.charAt(i));
            if ((i == 1 || i == 3) && i + 1 < digits.length()) {
                formatted.append("/");
            }
        }
        return formatted.toString();
    }

    private LocalDate parseDate(String date) {
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) return null;
        try {
            String[] parts = date.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            return LocalDate.of(year, Math.min(12, Math.max(1, month)), Math.min(YearMonth.of(year, month).lengthOfMonth(), Math.max(1, day)));
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }

    @FXML
    private void onHomeClicked() {
        NavigationService.navigateTo("HomePage.fxml", "Home");
    }

    @FXML
    private void personalDetailsPopup() {
        togglePopup(personalDetailsVbox, pdStatusImg);
    }

    @FXML
    private void academicQualificationPopup() {
        togglePopup(academicalQualificationVbox, aqStatusImg);
    }

    private void togglePopup(VBox vbox, ImageView statusImg) {
        boolean isVisible = !vbox.isVisible();
        vbox.setVisible(isVisible);
        vbox.setManaged(isVisible);
        if (statusImg != null) {
            statusImg.setImage(isVisible ? checkImage : exclamationImage);
        }
    }

    @FXML
    private void handleDragOverPassport(DragEvent event) {
        handleDragOver(event);
    }

    @FXML
    private void handleDropPassport(DragEvent event) {
        handleFileDrop(event, passportField, passportDropArea);
    }

    @FXML
    private void uploadPassport() {
        uploadFile(passportField, passportDropArea);
    }

    @FXML
    private void handleDragOverDiploma(DragEvent event) {
        handleDragOver(event);
    }

    @FXML
    private void handleDropDiploma(DragEvent event) {
        handleFileDrop(event, diplomaField, diplomaDropArea);
    }

    @FXML
    private void uploadDiploma() {
        uploadFile(diplomaField, diplomaDropArea);
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleFileDrop(DragEvent event, TextField textField, Label label) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            textField.setText(file.getName());
            label.setText("File: " + file.getName());
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void uploadFile(TextField textField, Label label) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF, Images", "*.pdf", "*.jpg", "*.png"));
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            textField.setText(selectedFile.getName());
            label.setText("File: " + selectedFile.getName());
        }
    }
}
