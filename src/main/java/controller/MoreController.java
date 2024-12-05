package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MoreController {

    // Nếu bạn đã khai báo fx:id cho các nút trong FXML, hãy khai báo chúng ở đây
    @FXML
    private Button homeButton;

    @FXML
    private Button borrowedButton;

    @FXML
    private Button moreButton;

    /**
     * Phương thức xử lý sự kiện khi nhấn nút "HOME".
     */
    @FXML
    public void switchToHome(ActionEvent event) {
        try {
            // Tải FXML của giao diện HOME
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) homeButton.getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Không thể chuyển đến giao diện HOME.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Phương thức xử lý sự kiện khi nhấn nút "BORROWED".
     */
    @FXML
    public void switchToBorrowed(ActionEvent event) {
        try {
            // Tải FXML của giao diện BORROWED
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowedDocuments.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) borrowedButton.getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Không thể chuyển đến giao diện BORROWED.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Phương thức xử lý sự kiện khi nhấn nút "MORE".
     */
    @FXML
    private void handleMoreButton(ActionEvent event) {
        // Bạn có thể thêm chức năng cụ thể cho nút "MORE" tại đây
        // Ví dụ: Mở một cửa sổ chi tiết hoặc thực hiện hành động khác
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/moreDetails.fxml"));
            Parent root = loader.load();

            // Tạo Stage mới cho cửa sổ chi tiết
            Stage stage = new Stage();
            stage.setTitle("More Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Ngăn tương tác với cửa sổ chính
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Không thể mở cửa sổ More Details.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Phương thức hiển thị Alert.
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
