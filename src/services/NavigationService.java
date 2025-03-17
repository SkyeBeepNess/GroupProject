package services;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class NavigationService {
	public static void navigateTo(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationService.class.getResource("/views/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElse(null);

            if (stage != null) {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle(title);
                stage.show();
            } else {
                System.out.println("Ошибка: Нет активного окна для переключения сцены!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}