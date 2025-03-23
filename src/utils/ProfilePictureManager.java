package utils;

import dbhandlers.ApplicantDAO;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Applicant;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ProfilePictureManager {

    private final ImageView profileImageView;
    private final VBox dropArea;
    private final HBox imageBox;
    private final Button updateButton;
    private final Button deleteButton;
    private final boolean isAdminView;
    private final Image defaultProfileImage;

    private final ApplicantDAO applicantDAO;
    private final Applicant applicant;

    public ProfilePictureManager(Applicant applicant,
                                 ApplicantDAO applicantDAO,
                                 ImageView profileImageView,
                                 VBox dropArea,
                                 HBox imageBox,
                                 Button updateButton,
                                 Button deleteButton,
                                 boolean isAdminView,
                                 Image defaultProfileImage) {
        this.applicant = applicant;
        this.applicantDAO = applicantDAO;
        this.profileImageView = profileImageView;
        this.dropArea = dropArea;
        this.imageBox = imageBox;
        this.updateButton = updateButton;
        this.deleteButton = deleteButton;
        this.isAdminView = isAdminView;
        this.defaultProfileImage = defaultProfileImage;
    }

    public void initialize() {
        updateUI(null, true);
    }

    public void loadFromBase64(String base64) {
        if (base64 != null && !base64.isBlank()) {
            byte[] bytes = Base64.getDecoder().decode(base64);
            updateUI(new Image(new ByteArrayInputStream(bytes)), false);
        } else {
            updateUI(defaultProfileImage, true);
        }
    }

    public void clearImage() {
        updateUI(isAdminView ? defaultProfileImage : null, isAdminView);

        if (applicant != null) {
            applicant.setProfilePicture(null);
            applicantDAO.updateProfilePicture(applicant.getUserId(), null);
        }
    }

    public void setImageFromFile(File file) {
        try {
            if (file == null) {
                clearImage();
                return;
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            Image image = new Image(new ByteArrayInputStream(bytes));
            updateUI(image, false);

            if (applicant != null) {
                applicant.setProfilePicture(base64);
                applicantDAO.updateProfilePicture(applicant.getUserId(), base64);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUI(Image image, boolean isDefault) {
        profileImageView.setImage(image);

        boolean hasImage = image != null;

        dropArea.setVisible(!hasImage && !isAdminView);
        dropArea.setManaged(!hasImage && !isAdminView);

        imageBox.setVisible(hasImage);
        imageBox.setManaged(hasImage);

        if (isAdminView) {
            updateButton.setVisible(false);
            updateButton.setManaged(false);

            deleteButton.setVisible(!isDefault);
            deleteButton.setManaged(!isDefault);
        }
    }
}
