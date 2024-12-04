package controller;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML
    private Label messageLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailTextField;

    @FXML
    private Button resetPasswordButton;

    private UserDAO userDAO = new UserDAO(); // Khởi tạo UserDAO

    /**
     * Phương thức xử lý sự kiện khi nhấn nút "Reset Password".
     */
    @FXML
    private void resetPassword(ActionEvent event) {
        String username = usernameTextField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailTextField.getText().trim();

        // Kiểm tra các trường có bị bỏ trống không
        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu trùng nhau
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        // Kiểm tra xem User Name và Email có tồn tại và tương ứng cùng một người dùng không
        boolean userExists = userDAO.updatePasswordIfUserExists(username, email, newPassword);

        if (userExists) {
            // Nếu cập nhật thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Password has been reset successfully.");
            alert.showAndWait();

            // Đóng cửa sổ Forgot Password
            Stage stage = (Stage) resetPasswordButton.getScene().getWindow();
            stage.close();

            // Optional: Chuyển hướng sang màn hình đăng nhập
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();
                Stage primaryStage = (Stage) resetPasswordButton.getScene().getWindow();
                primaryStage.setScene(new Scene(root, 400, 600));
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Nếu không tìm thấy người dùng hoặc thông tin không khớp
            messageLabel.setText("Username and Email do not match our records.");
        }
    }
}
