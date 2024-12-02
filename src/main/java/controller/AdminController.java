package controller;

import Objects.Document;
import Objects.User;
import dao.BookDAO;
import dao.UserDAO;
import dao.Statistics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button sumUserButton;

    @FXML
    private Button sumBookButton;

    @FXML
    private Button sumRequestButton;

    @FXML
    private VBox userContainer;

    @FXML
    private Button requestButton;

    List<User> recommended;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hiển thị thống kê
        loadStatistics();

        // Hiển thị danh sách người dùng
        loadUsers();
    }

    private void loadStatistics() {
        sumUserButton.setText("    " + Statistics.getTotalUsers());
        sumBookButton.setText("    " + Statistics.getTotalBooks());
        sumRequestButton.setText("    " + Statistics.getTotalRequests());
    }

    private void loadUsers() {
        recommended = users();
        System.out.println(recommended.toString());
        System.out.println(recommended.size());

        try {
            for (User user : recommended) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/users.fxml"));
                HBox userBox = fxmlLoader.load();

                userBox.getStylesheets().add(getClass().getResource("/css/userStyling.css").toExternalForm());

                UserController userController = fxmlLoader.getController();
                userController.loadUserInfo(user);

                userContainer.getChildren().add(userBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> users() {
        List<User> ls = new ArrayList<>();
        UserDAO userDAO = new UserDAO();
        ls = userDAO.getAllUsers();
        return ls;
    }

    @FXML
    private void search(ActionEvent event) {
        // Xử lý tìm kiếm tại đây
    }




    //huyen
    @FXML
    private void handleRequestButton() throws IOException {
        Stage stage = (Stage) requestButton.getScene().getWindow();

        Parent requestRoot = FXMLLoader.load(getClass().getResource("/fxml/requestsScreen.fxml"));

        Scene scene = new Scene(requestRoot, 900, 600);

        stage.setScene(scene);
    }
}
