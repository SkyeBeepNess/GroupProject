package services;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UIServices {
	
	public static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	

}
