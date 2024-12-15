package controller;

import dao.UserDAO;
import Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProfileController {

    @FXML private ImageView profileImageView;
    @FXML private Label usernameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label usernameDetailLabel;
    @FXML private Label emailLabel;
    @FXML private Button editProfileButton;
    @FXML private Button viewHistoryButton;
    @FXML private Button logoutButton;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextArea feedbackTextArea;

    private int userId; // Biến lưu trữ userId
    private UserDAO userDAO = new UserDAO(); // Khởi tạo UserDAO

    @FXML
    public void initialize() {
        // Nếu cần khởi tạo giá trị mặc định, thực hiện tại đây.
    }


    @FXML
    public void setUserId(int userId) {
        System.out.println("Setting user ID: " + userId);  // In ra userId
        this.userId = userId;
        loadUserData();
    }

    private void loadUserData() {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            // Gán thông tin người dùng

            fullNameLabel.setText(user.getFullName());
            usernameDetailLabel.setText(user.getUserName());  // Gán userName vào usernameDetailLabel
            emailLabel.setText(user.getEmail());

            // Gán avatar cho profileImageView
            String avatarPath = user.getPathAvatar();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                Image profileImage = new Image("file:" + avatarPath);
                profileImageView.setImage(profileImage);
            } else {
                // Đặt ảnh mặc định nếu không có avatar
                Image profileImage = new Image(getClass().getResourceAsStream("/image/bin.png"));
                profileImageView.setImage(profileImage);
            }
        } else {
            // Xử lý nếu người dùng không tìm thấy
            usernameLabel.setText("User not found");
        }
    }


    @FXML
    private void handleChangeAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            Image image = new Image("file:" + selectedFile.getAbsolutePath());
            profileImageView.setImage(image);

            User user = userDAO.getUserById(userId);
            if (user != null) {
                user.setPathAvatar(selectedFile.getAbsolutePath());
                userDAO.updateUser(user);
            }
        }
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        String fullName = fullNameField.getText();
        String email = emailField.getText();

        if (fullName.isEmpty() || email.isEmpty()) {
            showAlert("Error", "Full Name and Email cannot be empty.", Alert.AlertType.WARNING);
            return;
        }

        User user = userDAO.getUserById(userId);
        if (user != null) {
            user.setFullName(fullName);
            user.setEmail(email);
            userDAO.updateUser(user);
            loadUserData();
            showAlert("Success", "Profile updated successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "User not found.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSubmitFeedback(ActionEvent event) {
        String feedback = feedbackTextArea.getText();

        if (feedback.isEmpty()) {
            showAlert("Error", "Please enter feedback before submitting.", Alert.AlertType.WARNING);
            return;
        }

        try (FileWriter writer = new FileWriter("feedback.txt", true)) {
            writer.write("Feedback from User ID " + userId + ": " + feedback + "\n");
            feedbackTextArea.clear();
            showAlert("Success", "Feedback submitted successfully!", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to submit feedback.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void switchToBorrowed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowedDocuments.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToMore(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/More.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
