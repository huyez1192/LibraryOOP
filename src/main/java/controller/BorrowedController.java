package controller;

import Objects.BorrowRecord;
import Objects.Document;
import connect.MySQLConnection;
import dao.BorrowRecordDAO;
import dao.RequestDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class BorrowedController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

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
            RequestDAO requestDAO = new RequestDAO();
            boolean requestExists = requestDAO.checkIfRequestExists(borrowRecord.getUserId(), borrowRecord.getIsbn());

            BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO();
            boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(borrowRecord.getUserId(), borrowRecord.getIsbn());
            Document document = new Document(borrowRecord.getIsbn());
            if (borrowRecord.getReturnDate() == null) {
                requestExists = false;
                borrowedExists = true;
            } else {
                requestExists = requestExists = false;
                borrowedExists = false;
            }

            // Lấy controller của bookdetails.fxml
            BookDetailsController detailsController = loader.getController();
            detailsController.loadBookDetails(document, borrowRecord.getUserId(), requestDAO, borrowRecordDAO);

            // Tạo một Stage mới để hiển thị giao diện chi tiết sách
            Stage stage = new Stage();
            stage.setTitle("Document Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<BorrowRecord> borrowedList = FXCollections.observableArrayList();

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("documentTitle"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
    }

    @FXML
    private void onSearch() {
        // Handle search functionality when "Search" button is clicked
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            System.out.println("Searching for: " + query);
            // Implement the logic to filter the table data based on the query
        } else {
            System.out.println("Search field is empty.");
        }
    }

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

    private void loadDataFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/libraryy";
        String username = "root";
        String password = "";

        String query = """
            SELECT b.title, br.isbn, br.borrow_date, br.return_date
            FROM borrowedbooks br
            JOIN books b ON br.isbn = b.isbn
        """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String title = rs.getString("title");
                String isbn = rs.getString("isbn");
                // Xử lý Borrow Date và Return Date
                Date borrowDate = (rs.getDate("borrow_date") != null) ? rs.getDate("borrow_date") : new Date(0); // Giá trị mặc định
                Date returnDate = (rs.getDate("return_date"));



                borrowedList.add(new BorrowRecord(title, isbn, borrowDate, returnDate));
            }

            borrowedTable.setItems(borrowedList);

        } catch (SQLException e) {
            e.printStackTrace();
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
