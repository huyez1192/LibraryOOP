package controller;

import Objects.BorrowRecord;
import Objects.Document;
import connect.MySQLConnection;
import dao.BorrowRecordDAO;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.UserIdSingleton;

import java.io.IOException;
import java.sql.*;

public class BorrowedController extends Controller {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button moreButton;

    @FXML
    private TableView<BorrowRecord> borrowedTable;

    @FXML
    private TableColumn<BorrowRecord, String> titleColumn;

    @FXML
    private TableColumn<BorrowRecord, String> isbnColumn;

    @FXML
    private TableColumn<BorrowRecord, Date> borrowDateColumn;

    @FXML
    private TableColumn<BorrowRecord, Date> returnDateColumn;

    private int userId = UserIdSingleton.getInstance().getUserId();
    // Initialize method is called automatically after the FXML is loaded
    @FXML
    public void initialize() {
        setupTableColumns();
        loadDataFromDatabase();
        borrowedTable.setOnMouseClicked(this::handleRowDoubleClick);
    }

    private void handleRowDoubleClick(javafx.scene.input.MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) { // Kiểm tra nếu là nhấn đúp chuột
            BorrowRecord selectedDocument = borrowedTable.getSelectionModel().getSelectedItem();
            if (selectedDocument != null) {
                showDocumentDetails(selectedDocument);
            }
        }
    }

    private void showDocumentDetails(BorrowRecord borrowRecord) {
        try {
            // Load giao diện bookdetails.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookDetail.fxml"));
            Parent root = loader.load();

            // Lấy controller của bookdetails.fxml
            BookDetailsController detailsController = loader.getController();

            // Lấy thông tin từ bảng book bằng isbn
            String isbn = borrowRecord.getIsbn();
            Document document = getDocumentByIsbn(isbn);  // Hàm lấy thông tin sách từ DB

            // Gửi thông tin cần thiết vào BookDetailsController
            detailsController.loadBookDetails(document, borrowRecord.getUserId(), new RequestDAO(), new BorrowRecordDAO());

            // Tạo một Stage mới để hiển thị giao diện chi tiết sách
            Stage stage = new Stage();
            stage.setTitle("Document Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document getDocumentByIsbn(String isbn) {
        String query = "SELECT title, authors, categories, description, thumbnail_link FROM books WHERE isbn = ? AND user_id = ?";
        Document document = null;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/libraryy", "root", "huyen16125");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, isbn);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String authors = rs.getString("authors");
                    String categories = rs.getString("categories");
                    String description = rs.getString("description");
                    String thumbnailLink = rs.getString("thumbnail_link");

                    document = new Document(isbn, title, authors, categories, description, thumbnailLink);
                } else {
                    System.err.println("No document found for ISBN: " + isbn + " and userID: " + userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return document;
    }





    private ObservableList<BorrowRecord> borrowedList = FXCollections.observableArrayList();

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("documentTitle"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
    }

    @FXML
    private void switchToUserMore() {
        switchScene("/fxml/UserMore.fxml", borrowedTable);
    }

    public void switchToUserHome() {
        switchScene("/fxml/library.fxml", borrowedTable);
    }

//    @FXML
//    private void switchToUserRequests() {
//        switchScene("/fxml/userRequests.fxml", borrowedTable);
//    }

    // Phương thức chuyển sang giao diện Profile
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
    public void switchToRequested(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/requested.fxml"));
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

    private void loadDataFromDatabase() {
        Document document = null;

        String query = """
                    SELECT b.title, br.isbn, br.borrow_date, br.return_date
                    FROM borrowedbooks br
                    JOIN books b ON br.isbn = b.isbn
                    WHERE br.user_id = ?
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/libraryy", "root", "huyen16125");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { // Sử dụng while để duyệt qua toàn bộ các bản ghi
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    Date borrowDate = (rs.getDate("borrow_date") != null) ? rs.getDate("borrow_date") : new Date(0);
                    Date returnDate = rs.getDate("return_date");

                    borrowedList.add(new BorrowRecord(title, isbn, borrowDate, returnDate));
                }
                borrowedTable.setItems(borrowedList);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void searchDocuments(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a keyword to search.");
            alert.showAndWait();
            return;
        }

        borrowedList.clear(); // Xóa dữ liệu cũ trước khi tải mới

        try (Connection connection = MySQLConnection.getConnection()) {
            String sql = "SELECT b.title, br.isbn, br.borrow_date, br.return_date\n" +
                    "            FROM borrowedbooks br\n" +
                    "            JOIN books b ON br.isbn = b.isbn\n" +
                    "            WHERE title LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "%" + query + "%");

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String title = rs.getString("title");
                        String isbn = rs.getString("isbn");
                        Date borrowDate = (rs.getDate("borrow_date") != null) ? rs.getDate("borrow_date") : new Date(0); // Giá trị mặc định
                        Date returnDate = (rs.getDate("return_date"));

                        BorrowRecord document = new BorrowRecord(title, isbn, borrowDate, returnDate);
                        borrowedList.add(document); // Thêm sách vào danh sách
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to search documents from database.");
            alert.showAndWait();
        }

        // Cập nhật dữ liệu vào TableView
        borrowedTable.setItems(borrowedList);
    }
}