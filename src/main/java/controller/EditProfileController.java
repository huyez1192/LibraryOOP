package controller;

import dao.UserDAO;
import Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditProfileController {

    @FXML
    private Label messageLabel;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button saveButton;

    private int userId; // Biến lưu trữ userId

    private UserDAO userDAO = new UserDAO(); // Khởi tạo UserDAO

    /**
     * Thiết lập userId và tải thông tin người dùng vào các trường nhập liệu.
     *
     * @param userId ID của người dùng
     */
    public void setUserId(int userId) {
        this.userId = userId;
        loadUserData();
    }

    /**
     * Tải dữ liệu người dùng và hiển thị vào các trường nhập liệu.
     */
    private void loadUserData() {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            fullNameField.setText(user.getFullName());
            usernameField.setText(user.getUserName());
            emailField.setText(user.getEmail());
            // Không điền mật khẩu vì lý do bảo mật
        } else {
            messageLabel.setText("User not found.");
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Save Changes".
     */
    @FXML
    private void handleSaveChanges(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Kiểm tra các trường có bị bỏ trống không
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Full Name, Username, and Email cannot be empty.");
            return;
        }

        // Kiểm tra nếu người dùng muốn đổi mật khẩu
        boolean changePassword = false;
        String passwordToSave = null;
        if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                messageLabel.setText("New Password and Confirm Password do not match.");
                return;
            }
            // Không băm mật khẩu, lưu trực tiếp
            passwordToSave = newPassword;
            changePassword = true;
        }

        // Tạo đối tượng User mới để cập nhật
        User updatedUser = new User();
        updatedUser.setUserId(userId);
        updatedUser.setFullName(fullName);
        updatedUser.setUserName(username);
        updatedUser.setEmail(email);
        if (changePassword) {
            updatedUser.setPassword(passwordToSave);
        } else {
            // Nếu không đổi mật khẩu, lấy mật khẩu hiện tại từ cơ sở dữ liệu
            User existingUser = userDAO.getUserById(userId);
            if (existingUser != null) {
                updatedUser.setPassword(existingUser.getPassword());
            }
        }

        // Cập nhật người dùng trong cơ sở dữ liệu
        userDAO.updateUser(updatedUser);

        // Hiển thị thông báo thành công
        messageLabel.setText("Profile updated successfully.");

        // Đóng cửa sổ sau khi cập nhật
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
