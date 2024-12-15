package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.Parent;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static javax.swing.JOptionPane.showMessageDialog;

public class SignupController {
    @FXML
    private Hyperlink loginLink;
    @FXML
    private TextField fullnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailAddress;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField confirmPasswordPasswordField;
    @FXML
    private Button signupButton;


    @FXML
    private void handleLoginLink() throws IOException {
        Stage stage = (Stage) loginLink.getScene().getWindow();

        Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        Scene scene = new Scene(loginRoot, 400, 600);

        stage.setScene(scene);
    }

    public void SignUpButtonActionPerformed(ActionEvent event) {
        String fullName, username, password, query, email;
        String URL, USER, PASSWORD;
        URL = "jdbc:mysql://localhost:3306/library";
        USER = "root";
        PASSWORD = "gem07012005";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            if ("".equals(fullnameTextField.getText())) {
                showMessageDialog(new JFrame(), "Full name is require", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(usernameTextField.getText())) {
                showMessageDialog(new JFrame(), "Username is require", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(passwordPasswordField.getText())) {
                showMessageDialog(new JFrame(), "Password is require", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(emailAddress.getText())) {

                showMessageDialog(new JFrame(), "Email is require", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(confirmPasswordPasswordField.getText())) {
                showMessageDialog(new JFrame(), "Confirm password is require", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!confirmPasswordPasswordField.getText().equals(passwordPasswordField.getText())) {
                showMessageDialog(new JFrame(), "Password does not match", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                fullName = fullnameTextField.getText();
                username = usernameTextField.getText();
                email = emailAddress.getText();
                password = passwordPasswordField.getText();

                query = "INSERT INTO Users(full_name, user_name, pass_word, email) " +
                        "VALUES('" + fullName + "', '" + username + "', '" + password + "', '" + email + "')";

                statement.execute(query);
                fullnameTextField.setText("");
                usernameTextField.setText("");
                emailAddress.setText("");
                passwordPasswordField.setText("");
                showMessageDialog(null, "New account has been created successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error!" + e.getMessage());
        }
    }
}
