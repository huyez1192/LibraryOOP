package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UserMoreController {
    private int userId;

    public void switchToHome(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToBorrowed(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowedDocuments.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToProfile(ActionEvent event) {
        try {
            // Tải FXML của giao diện Profile
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();

            // Lấy controller của ProfileController để truyền userId
            ProfileController profileController = loader.getController();
            profileController.setUserId(userId); // Đảm bảo userId đã được thiết lập

            // Tạo và hiển thị Stage mới cho Profile
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open Profile window.", Alert.AlertType.ERROR);
        }
    }

    // Hiển thị thông báo
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> { // Đảm bảo chạy trên UI thread
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


    @FXML
    public void switchToRequested(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/requested.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
