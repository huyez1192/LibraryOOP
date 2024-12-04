package controller;

import Objects.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.swing.text.html.ImageView;

public class UserController {
    @FXML
    private ImageView delete;

    @FXML
    private TextField name;

    @FXML
    private TextField username;

    @FXML
    private TextField quantityBorrowing;

    @FXML
    private TextField quantityBorrowed;

    public void loadUserInfo(User user) {
        name.setText(user.getFullName());
        username.setText(user.getUserName());
        quantityBorrowed.setText("0");
        quantityBorrowing.setText("0");
    }
}
