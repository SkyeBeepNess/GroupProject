package services;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

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
	
	public static ButtonType showConfirmation(String title, String question, String btn1txt, String btn2txt) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(question);
        ButtonType btn1 = new ButtonType(btn1txt);
        ButtonType btn2 = new ButtonType(btn2txt);
        alert.getButtonTypes().set(0, btn2);
        alert.getButtonTypes().set(1, btn1);
        alert.showAndWait();
        return alert.getResult();
    }
	

}
