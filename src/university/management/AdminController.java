package university.management;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;

public class AdminController {
    @FXML private TableView<Applicant> adminTable;
    @FXML private TableColumn<Applicant, String> nameColumn;
    @FXML private TableColumn<Applicant, String> dateColumn;
    @FXML private TableColumn<Applicant, String> certificateColumn;
    @FXML private TableColumn<Applicant, String> gradeColumn;
    @FXML private TableColumn<Applicant, String> statusColumn;
    @FXML private Button viewButton;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        certificateColumn.setCellValueFactory(new PropertyValueFactory<>("certificate"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

      
        ObservableList<Applicant> applicants = DatabaseManager.getAllApplicants();
        adminTable.setItems(applicants);
    }

    @FXML
    private void viewApplication() {
        Applicant selectedApplicant = adminTable.getSelectionModel().getSelectedItem();
        if (selectedApplicant == null) {
            System.out.println("Choose applicant!");
            return;
        }

        try {
            System.out.println("Opens applicant_detail.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("applicant_detail.fxml"));

            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found!");
            }

            Parent root = loader.load();

            ApplicantDetailController controller = loader.getController();
            controller.setApplicant(selectedApplicant);

            Stage stage = (Stage) viewButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Applicant Details");

            System.out.println("Applicant Details for: " + selectedApplicant.getName());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while loading applicant_detail.fxml: " + e.getMessage());
        }
    }


    @FXML
    private void approveApplication() {
        changeApplicationStatus("Approved");
    }

    @FXML
    private void rejectApplication() {
        changeApplicationStatus("Rejected");
    }

    private void changeApplicationStatus(String newStatus) {
        Applicant selectedApplicant = adminTable.getSelectionModel().getSelectedItem();
        if (selectedApplicant == null) {
            System.out.println("Choose applicant!");
            return;
        }

        DatabaseManager.updateApplicationStatus(selectedApplicant.getName(), newStatus);
        selectedApplicant.setStatus(newStatus); 
        adminTable.refresh();
    }
}
