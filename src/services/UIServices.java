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
	
	public static void showWorkings() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("In progress");
        alert.setHeaderText(null);
        alert.setContentText("This section is not ready yet");
        alert.showAndWait();
    }
	

}
