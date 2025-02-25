package university.management;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ApplicantDetailController {
    @FXML private Label nameLabel;
    @FXML private Label dateLabel;
    @FXML private Label certificateLabel;
    @FXML private Label gradeLabel;  // Добавляем Grade
    @FXML private Label statusLabel;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;

    private Applicant applicant;

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
        nameLabel.setText(applicant.getName());
        dateLabel.setText(applicant.getDate());
        certificateLabel.setText(applicant.getCertificate());
        gradeLabel.setText(applicant.getGrade()); // Заполняем Grade
        statusLabel.setText(applicant.getStatus());
    }

    @FXML
    private void approveApplication() {
        updateApplicationStatus("Approved");
    }

    @FXML
    private void rejectApplication() {
        updateApplicationStatus("Rejected");
    }

    private void updateApplicationStatus(String status) {
        if (applicant == null) return;

        String sql = "UPDATE applicants SET Status = ? WHERE `Applicant Name` = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, applicant.getName());
            stmt.executeUpdate();

            statusLabel.setText(status);
            System.out.println("Application updated to: " + status);
        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
        }
    }
}
