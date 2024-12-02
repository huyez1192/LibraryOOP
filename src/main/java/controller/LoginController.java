package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class LoginController {

    @FXML
    private Hyperlink signUpLink; // Liên kết tới nút Sign Up
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private int userId;  // Thêm biến lưu userId

    // Phương thức chuyển đến trang Sign Up khi click vào liên kết Sign Up
    @FXML
    private void handleSignUpLink() throws IOException {
        Stage stage = (Stage) signUpLink.getScene().getWindow();
        Parent signUpRoot = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
        Scene scene = new Scene(signUpRoot, 400, 600);
        stage.setScene(scene);
    }

    // Phương thức xử lý khi người dùng click nút Login
    @FXML
    public void loginButtonOnAction(ActionEvent e) {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        // Kiểm tra nếu username hoặc password bị bỏ trống
        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Please fill in both username and password.");
            return;
        }

        // Kết nối đến cơ sở dữ liệu
        String URL = "jdbc:mysql://localhost:3306/library";
        String USER = "root";
        String PASSWORD = "gem07012005";
        String query = "SELECT user_id, pass_word FROM Users WHERE user_name = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Sử dụng PreparedStatement để tránh SQL Injection
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String passDb = resultSet.getString("pass_word");
                userId = resultSet.getInt("user_id");  // Lưu userId từ cơ sở dữ liệu

                // Kiểm tra mật khẩu
                if (password.equals(passDb)) {
                    // Đăng nhập thành công, chuyển sang trang chính và truyền userId
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library.fxml"));
                    Parent libraryRoot = loader.load();

                    // Truyền userId sang controller của trang chính (library)
                    HomeController homeController = loader.getController();
                    homeController.setUserId(userId);  // Gửi userId cho HomeController

                    Scene scene = new Scene(libraryRoot, 900, 600);
                    stage.setScene(scene);
                } else {
                    // Nếu mật khẩu không đúng
                    loginMessageLabel.setText("Incorrect username or password!");
                }
            } else {
                // Nếu không tìm thấy người dùng
                loginMessageLabel.setText("User not found!");
            }

        } catch (Exception ex) {
            // Xử lý lỗi kết nối cơ sở dữ liệu
            loginMessageLabel.setText("Database connection error!");
            ex.printStackTrace();
        }
    }

    // Phương thức để lấy userId khi cần
    public int getUserId() {
        return userId;
    }
}
