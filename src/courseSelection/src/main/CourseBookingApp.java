package courseSelection.src.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CourseBookingApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // ✅ Load the Login screen (make sure Login.fxml is in /courseselection folder)
        Parent root = FXMLLoader.load(getClass().getResource("courseSelection/src/courseselection/Login.fxml"));
        primaryStage.setTitle("UMS Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
