//package controller;
//
//import Objects.Request;
//import dao.RequestDAO;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.input.MouseButton;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class RequestedController {
//    public TableView<Request> tableView;
//    public TableColumn<Request, String> colIsbn;
//    public TableColumn<Request, String> colBook;
//    public TableColumn<Request, String> colAuthor;
//    public TableColumn<Request, String> colResponse;
//
//    private final ObservableList<Request> requests = FXCollections.observableArrayList();
//
//    public void initialize(int userId) {
//        // Thiết lập cột
//        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
//        colBook.setCellValueFactory(new PropertyValueFactory<>("title"));
//        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
//        colResponse.setCellValueFactory(new PropertyValueFactory<>("response"));
//
//        // Tải dữ liệu
//        requests.setAll(RequestDAO.getRequestsByUserId(userId));
//        tableView.setItems(requests);
//
//        // Xử lý click chuột phải
//        tableView.setRowFactory(tv -> {
//            TableRow<Request> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
//                    Request request = row.getItem();
//                    ContextMenu contextMenu = new ContextMenu();
//
//                    if ("Requesting".equals(request.getResponse())) {
//                        MenuItem cancelItem = new MenuItem("Cancel request");
//                        cancelItem.setOnAction(e -> {
//                            RequestDAO.deleteRequest(request.getRequestId());
//                            requests.remove(request);
//                        });
//                        contextMenu.getItems().add(cancelItem);
//                    } else if ("Accepted".equals(request.getResponse())) {
//                        MenuItem closeItem = new MenuItem("Đóng");
//                        closeItem.setOnAction(e -> requests.remove(request));
//                        contextMenu.getItems().add(closeItem);
//                    }
//                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
//                }
//            });
//            return row;
//        });
//    }
//
//    @FXML
//    public void switchToBorrowed(ActionEvent event) {
//        try {
//            // Tải FXML của giao diện thứ hai
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowedDocuments.fxml"));
//            Parent root = loader.load();
//
//            // Lấy Stage hiện tại
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//            // Đổi Scene
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    public void switchToMore(ActionEvent event) {
//        try {
//            // Tải FXML của giao diện thứ hai
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserMore.fxml"));
//            Parent root = loader.load();
//
//            // Lấy Stage hiện tại
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//            // Đổi Scene
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void switchToHome(ActionEvent event) {
//        try {
//            // Tải FXML của giao diện thứ hai
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library.fxml"));
//            Parent root = loader.load();
//
//            // Lấy Stage hiện tại
//            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
//
//            // Đổi Scene
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}


package controller;

import Objects.Request;
import dao.RequestDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.UserIdSingleton;

import java.io.IOException;
import java.sql.*;

public class RequestedController {
    @FXML
    private TableView<Request> tableView;

    @FXML
    private TableColumn<Request, String> colIsbn;

    @FXML
    private TableColumn<Request, String> colAuthor;

    @FXML
    private TableColumn<Request, String> colResponse;

    @FXML
    private TableColumn<Request, String> colBook;

    @FXML
    private ObservableList<Request> requestedList = FXCollections.observableArrayList();

    private int userId = UserIdSingleton.getInstance().getUserId();

    @FXML
    public void switchToHome(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library.fxml"));
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

    @FXML
    public void switchToBorrowed(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowedDocuments.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToMore(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserMore.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToProfile(ActionEvent event) {
        try {
            // Tải FXML của giao diện Profile
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();

            // Lấy controller của ProfileController để truyền userId
            ProfileController profileController = loader.getController();
            profileController.setUserId(userId); // Đảm bảo userId đã được thiết lập

            // Tạo và hiển thị Stage mới cho Profile
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open Profile window.", Alert.AlertType.ERROR);
        }
    }

    // Hiển thị thông báo
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> { // Đảm bảo chạy trên UI thread
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


    @FXML
    public void initialize() {
        setupTableColumns();
        loadDataFromDatabase();



        // Xử lý click chuột phải
        tableView.setRowFactory(tv -> {
            TableRow<Request> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    Request request = row.getItem();
                    ContextMenu contextMenu = new ContextMenu();

                    if ("Requesting".equals(request.getResponse())) {
                        MenuItem cancelItem = new MenuItem("Cancel request");
                        cancelItem.setOnAction(e -> {
                            RequestDAO.deleteRequest(request.getRequestId());
                            requestedList.remove(request);
                        });
                        contextMenu.getItems().add(cancelItem);
                    }
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }


    private void setupTableColumns() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colBook.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("authors"));
        colResponse.setCellValueFactory(new PropertyValueFactory<>("response"));
    }

    private void loadDataFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/libraryy";
        String username = "root";
        String password = "huyen16125";

        String query = """
                SELECT r.request_id, b.isbn, b.title, b.authors,
                       CASE WHEN r.request_date IS NOT NULL THEN 'Requesting' ELSE 'Accepted' END AS response
                FROM requests r
                JOIN books b ON r.isbn = b.isbn
                WHERE r.user_id = ?;
                """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)){
//             ResultSet rs = stmt.executeQuery(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    String title = rs.getString("title");
                    String author = rs.getString("authors");
                    String response = rs.getString("response");

                    requestedList.add(new Request(isbn, title, author, response));
                }
                tableView.setItems(requestedList);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
            catch (SQLException e) {
                 throw new RuntimeException(e);
            }

    }
}