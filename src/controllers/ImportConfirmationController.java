package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImportConfirmationController {
    @FXML private Label fileLabel;
    @FXML private Button btnConfirm, btnCancel;

    private File selectedFile;
    private Stage stage;

    public void setFile(File file, Stage stage) {
        this.selectedFile = file;
        this.stage = stage;
        fileLabel.setText("Download: " + file.getName());
    }

    @FXML
    private void onFileClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName(selectedFile.getName());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File saveFile = fileChooser.showSaveDialog(stage);
        if (saveFile != null) {
            try {
                Files.copy(selectedFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File downloaded to: " + saveFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onConfirmClicked() {
        System.out.println("Importing file: " + selectedFile.getAbsolutePath());
        stage.close();
    }

    @FXML
    private void onCancelClicked() {
        stage.close();
    }
}
