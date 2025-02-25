package university.management;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ApplicantController {
    @FXML private TableView<Applicant> applicantTable;
    @FXML private TableColumn<Applicant, String> nameColumn;
    @FXML private TableColumn<Applicant, String> dateColumn;
    @FXML private TableColumn<Applicant, String> certificateColumn;
    @FXML private TableColumn<Applicant, String> gradeColumn;
    @FXML private TableColumn<Applicant, String> statusColumn;
    @FXML private Button uploadButton;
    @FXML private Button exitButton;

    private String applicantName;

    public void setApplicantName(String name) {
        this.applicantName = name;
        loadApplicantData();
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        certificateColumn.setCellValueFactory(new PropertyValueFactory<>("certificate"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadApplicantData() {
        if (applicantName == null) {
            System.out.println("No applicant name!");
            return;
        }

        
        ObservableList<Applicant> applicants = DatabaseManager.getApplicantByName(applicantName);
        applicantTable.setItems(applicants);
    }

    @FXML
    private void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            System.out.println("File selected: " + file.getAbsolutePath());
        } else {
            System.out.println("No file selected.");
        }
    }

    @FXML
    private void exitApplication() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
