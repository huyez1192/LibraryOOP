package linkui;

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

//import java.awt.event.ActionEvent;
import java.io.IOException;
import javafx.scene.Parent;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import static javax.swing.JOptionPane.showMessageDialog;

public class logincontroller {

    @FXML
    private Hyperlink signUpLink; // Liên kết tới nút Sign Up
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button loginButton;

    @FXML
    private void handleSignUpLink() throws IOException {
        Stage stage = (Stage) signUpLink.getScene().getWindow();

        Parent signUpRoot = FXMLLoader.load(getClass().getResource("/signup.fxml"));

        Scene scene = new Scene(signUpRoot, 400, 600);

        stage.setScene(scene);
    }

    public void loginButtonOnAction(ActionEvent e) {
        String username, password, query, passDb = null;
        String URL, USER, PASSWORD;
        URL = "jdbc:mysql://localhost:3306/libraryy";
        USER = "root";
        PASSWORD = "caohuongiang171";

        int notFound = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            if ("".equals(usernameTextField.getText())) {
                loginMessageLabel.setText("You try to login!");
            } else if ("".equals(passwordField.getText())) {
                loginMessageLabel.setText("You try to login!");
            } else {
                username = usernameTextField.getText();
                password = passwordField.getText();

                query = "SELECT * FROM useraccounts WHERE user_name = '" + username + "'";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    passDb = resultSet.getString("pass_word");
                    notFound = 1;
                }
                if (notFound == 1 && password.equals(passDb)) {
                    //System.out.println("OKAY!");
                    Stage stage = (Stage) loginButton.getScene().getWindow();

                    Parent signUpRoot = FXMLLoader.load(getClass().getResource("/library.fxml"));

                    Scene scene = new Scene(signUpRoot, 900, 600);

                    stage.setScene(scene);
                } else {
                    loginMessageLabel.setText("Incorrect username or password!");
                }

                statement.execute(query);
                passwordField.setText("");

            }
        } catch (Exception event) {
            System.out.println("Error!" + event.getMessage());
        }

    }
}
