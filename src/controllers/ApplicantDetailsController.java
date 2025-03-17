package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ApplicantDetailsController {
	@FXML
    private VBox popupVBox1;

    @FXML
    private VBox popupVBox2;

    private boolean isPopup1Visible = false;
    private boolean isPopup2Visible = false;

    @FXML
    private void togglePopup1() {
        isPopup1Visible = !isPopup1Visible;
        popupVBox1.setVisible(isPopup1Visible);
        popupVBox1.setManaged(isPopup1Visible);
    }

    @FXML
    private void togglePopup2() {
        isPopup2Visible = !isPopup2Visible;
        popupVBox2.setVisible(isPopup2Visible);
        popupVBox2.setManaged(isPopup2Visible);
    }
}
