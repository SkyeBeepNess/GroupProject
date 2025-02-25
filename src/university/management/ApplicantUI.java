package university.management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicantUI extends Application {
    private String applicantName;

    public ApplicantUI(String applicantName) {
        this.applicantName = applicantName;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/university.management/applicant_view.fxml"));
            Parent root = loader.load();

            ApplicantController controller = loader.getController();
            controller.setApplicantName(applicantName);

            primaryStage.setTitle("Applicant Dashboard");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while loading applicant_view.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
