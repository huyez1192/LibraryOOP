package controller;

import dao.UserDAO;
import dao.BorrowRecordDAO;
import Objects.User;
import Objects.BorrowRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class ProfileController {

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label usernameDetailLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Button editProfileButton;

    @FXML
    private Button viewHistoryButton;

    @FXML
    private Button logoutButton;

    private int userId; // Biến lưu trữ userId

    private UserDAO userDAO = new UserDAO(); // Khởi tạo UserDAO
    private BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO(); // Khởi tạo BorrowRecordDAO

    /**
     * Phương thức này được gọi khi controller được khởi tạo.
     * Đảm bảo rằng `userId` đã được thiết lập trước khi gọi phương thức này.
     */
    @FXML
    public void initialize() {
        // Không thực hiện gì ở đây vì userId chưa được thiết lập
    }

    /**
     * Thiết lập `userId` và tải thông tin người dùng.
     *
     * @param userId ID của người dùng
     */
    public void setUserId(int userId) {
        this.userId = userId;
        loadUserData();
    }

    /**
     * Tải dữ liệu người dùng từ UserDAO và cập nhật giao diện.
     */
    private void loadUserData() {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            fullNameLabel.setText(user.getFullName());
            usernameDetailLabel.setText(user.getUserName());
            emailLabel.setText(user.getEmail());

            // Set the username label (display full name)
            usernameLabel.setText(user.getFullName());

            // Chọn ngẫu nhiên một trong bốn hình ảnh đại diện
            String[] profileImages = {
                    "/C:/Users/ASUS/IdeaProjects/LibraryOOP1/LibraryOOP/src/main/resources/0f85d7be-6628-42ea-8d5c-ca19b6bcfc71.jpg",
                    "/C:/Users/ASUS/IdeaProjects/LibraryOOP1/LibraryOOP/src/main/resources/0f85d7be-6628-42ea-8d5c-ca19b6bcfc71.jpg",
                    "/C:/Users/ASUS/IdeaProjects/LibraryOOP1/LibraryOOP/src/main/resources/0f85d7be-6628-42ea-8d5c-ca19b6bcfc71.jpg",
                    "/C:/Users/ASUS/IdeaProjects/LibraryOOP1/LibraryOOP/src/main/resources/0f85d7be-6628-42ea-8d5c-ca19b6bcfc71.jpg"
            };
            Random random = new Random();
            int index = random.nextInt(profileImages.length);
            Image profileImage = new Image(getClass().getResourceAsStream("/image/default_book.png"));
            profileImageView.setImage(profileImage);
        } else {
            usernameLabel.setText("User not found");
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Edit Profile".
     */
    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editProfile.fxml"));
            Parent root = loader.load();

            // Lấy controller của EditProfileController để truyền userId
            EditProfileController editProfileController = loader.getController();
            editProfileController.setUserId(userId);

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root, 500, 600));
            stage.initModality(Modality.APPLICATION_MODAL); // Ngăn tương tác với cửa sổ chính
            stage.showAndWait();

            // Sau khi đóng cửa sổ Edit Profile, tải lại dữ liệu người dùng
            loadUserData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open Edit Profile window.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Borrow History".
     */
    @FXML
    private void handleViewHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookHistory.fxml"));
            Parent root = loader.load();

            // Lấy controller của BorrowHistoryController để truyền userId
            BorrowHistoryController borrowHistoryController = loader.getController();
            borrowHistoryController.setUserId(userId);

            Stage stage = new Stage();
            stage.setTitle("Borrow History");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL); // Ngăn tương tác với cửa sổ chính
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open Borrow History window.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Logout".
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Library.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot logout and return to Login.", Alert.AlertType.ERROR);
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
