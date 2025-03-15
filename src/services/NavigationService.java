package services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationService {
    public static void navigateTo(String fxmlFile, String sceneTitle) {
        try {
        	FXMLLoader loader = new FXMLLoader(NavigationService.class.getClassLoader().getResource("views/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) root.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("University Management System - " + sceneTitle);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}