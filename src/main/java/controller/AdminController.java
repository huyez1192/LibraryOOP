package controller;

import Objects.Document;
import Objects.Request;
import Objects.User;
import dao.BookDAO;
import dao.RequestDAO;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.print.Book;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private Button sumUserButton;

    @FXML
    private Button requestButton;

    @FXML
    private Button sumBookButton;

    @FXML
    private Button sumRequestButton;

    @FXML
    private VBox userContainer;

    @FXML
    private VBox bookContainer;

    @FXML
    private VBox requestContainer;

    List<User> recommended1;

    List<Document> recommended2;

    List<Request> recommended3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hiển thị thống kê
        loadStatistics();

        // Hiển thị danh sách người dùng
        loadUsers();

        loadBooks();

        loadRequests();
    }

    private void loadStatistics() {
        sumUserButton.setText("    " + Statistics.getTotalUsers());
        sumBookButton.setText("    " + Statistics.getTotalBooks());
        sumRequestButton.setText("    " + Statistics.getTotalRequests());
    }

    private void loadUsers() {
        recommended1 = users();
        System.out.println(recommended1.toString());
        System.out.println(recommended1.size());

        try {
            for (User user : recommended1) {
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

    private void loadBooks() {
        recommended2 = books();
        System.out.println(recommended2.toString());
        System.out.println(recommended2.size());

        try {
            for (Document book : recommended2) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/books.fxml"));
                HBox userBox = fxmlLoader.load();

                userBox.getStylesheets().add(getClass().getResource("/css/bookStyling.css").toExternalForm());

                BooksController booksController = fxmlLoader.getController();
                booksController.loadBookInfo(book);

                bookContainer.getChildren().add(userBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Document> books() {
        List<Document> ls = new ArrayList<>();
        BookDAO bookDAO = new BookDAO();
        ls = bookDAO.getAllDocuments();
        return ls;
    }

    private void loadRequests() {
        recommended3 = requests();
        System.out.println(recommended3.toString());
        System.out.println(recommended3.size());

        try {
            for (Request request : recommended3) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/requests.fxml"));
                HBox userBox = fxmlLoader.load();

                userBox.getStylesheets().add(getClass().getResource("/css/bookStyling.css").toExternalForm());

                RequestController requestController = fxmlLoader.getController();
                requestController.loadRequestsInfo(request);

                requestContainer.getChildren().add(userBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Request> requests() {
        List<Request> ls = new ArrayList<>();
        RequestDAO requestDAO = new RequestDAO();
        ls = requestDAO.getAllRequests();
        return ls;
    }

    public void switchToAdminDocuments(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/libraryDocument.fxml"));
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

    public void switchToAdminRequests(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/requestsScreen.fxml"));
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

    public void switchToAdminUsers(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminUsers.fxml"));
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
}