package controller;

import Objects.BorrowRecord;
import Objects.Request;
import connect.MySQLConnection;
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


public class RequestsScreenController {
    @FXML
    private Button homeButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Request> requestTableView;

    @FXML
    private TableColumn<Request, Integer> columnRequestID;

    @FXML
    private TableColumn<Request, Integer> columnUserID;

    @FXML
    private TableColumn<Request, String> columnUserName;

    @FXML
    private TableColumn<Request, String> columnISBN;

    @FXML
    private TableColumn<Request, Date> columnRequestDate;

    @FXML
    private ObservableList<Request> requestList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        setupTableColumns();
        loadDataFromDatabase();



        // Tạo ContextMenu cho TableView
        ContextMenu contextMenu = new ContextMenu();

        // Tùy chọn "Accept"
        MenuItem acceptItem = new MenuItem("Accept");
        acceptItem.setOnAction(event -> {
            Request selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                acceptRequest(selectedRequest);
            }
        });

        // Tùy chọn "Reject"
        MenuItem rejectItem = new MenuItem("Reject");
        rejectItem.setOnAction(event -> {
            Request selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                rejectRequest(selectedRequest);
            }
        });

        // Thêm các tùy chọn vào ContextMenu
        contextMenu.getItems().addAll(acceptItem, rejectItem);

        // Gán ContextMenu cho TableView
        requestTableView.setRowFactory(tv -> {
            TableRow<Request> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }


    private void setupTableColumns() {
        columnRequestID.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        columnUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        columnUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        columnISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        columnRequestDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
    }

    @FXML
    private void onSearch() {
        // Handle search functionality when "Search" button is clicked
        String query = searchTextField.getText().trim();
        if (!query.isEmpty()) {
            System.out.println("Searching for: " + query);
            // Implement the logic to filter the table data based on the query
        } else {
            System.out.println("Search field is empty.");
        }
    }

    private void loadDataFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/libraryy";
        String username = "root";
        String password = "huyen16125";

        String query = """
            SELECT r.request_id, r.user_id, u.full_name, r.isbn, r.request_date
            FROM requests r
            JOIN users u ON r.user_id = u.user_id
        """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Integer requestId = rs.getInt("request_id");
                Integer userId = rs.getInt("user_id");
                String userName = rs.getString("full_name");
                String isbn = rs.getString("isbn");
                // Xử lý Borrow Date và Return Date
                Date requestDate = (rs.getDate("request_date") != null) ? rs.getDate("request_date") : new Date(0); // Giá trị mặc định
                //Date requestDate = (rs.getDate("return_date"));

                requestList.add(new Request(requestId, userId, userName, isbn, requestDate));
            }
            requestTableView.setItems(requestList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchUser(ActionEvent event) {
        // Lấy từ khóa tìm kiếm từ TextField
        String query = searchTextField.getText().trim().toLowerCase();

        // Kiểm tra nếu ô tìm kiếm trống
        if (query.isEmpty()) {
            // Hiển thị toàn bộ danh sách nếu không có từ khóa tìm kiếm
            requestTableView.setItems(requestList);
            return;
        }

        // Lọc danh sách theo tên người dùng
        ObservableList<Request> filteredList = FXCollections.observableArrayList();
        for (Request request : requestList) {
            if (request.getUserName().toLowerCase().contains(query)) {
                filteredList.add(request);
            }
        }

        // Cập nhật TableView với danh sách đã lọc
        requestTableView.setItems(filteredList);

        // Hiển thị thông báo nếu không tìm thấy kết quả
        if (filteredList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search Results");
            alert.setHeaderText(null);
            alert.setContentText("No requests found for the given user name.");
            alert.showAndWait();
        }
    }



    @FXML
    private void handleHomeButton() {
        try {
            Stage stage = (Stage) homeButton.getScene().getWindow();

            Parent homeRoot = FXMLLoader.load(getClass().getResource("/fxml/admindashboard.fxml"));

            Scene scene = new Scene(homeRoot, 900, 600);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    // Xử lý Accept Request
    private void acceptRequest(Request request) {
        try (Connection connection = MySQLConnection.getConnection()) {
            String insertSql = "INSERT INTO BorrowedBooks (user_id, isbn, borrow_date) VALUES (?, ?, ?)";
            String deleteSql = "DELETE FROM Requests WHERE request_id = ?";

            connection.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                 PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

                // Thêm thông tin vào bảng BorrowedBooks
                insertStmt.setInt(1, request.getUserId());
                insertStmt.setString(2, request.getIsbn());
                insertStmt.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now())); // Ngày hiện tại
                insertStmt.executeUpdate();

                // Xóa request khỏi bảng Requests
                deleteStmt.setInt(1, request.getRequestId());
                deleteStmt.executeUpdate();

                connection.commit(); // Commit transaction

                // Xóa dòng khỏi TableView
                requestList.remove(request);

            } catch (SQLException e) {
                connection.rollback(); // Rollback nếu có lỗi
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to accept request.");
        }
    }

    // Xử lý Reject Request
    private void rejectRequest(Request request) {
        try (Connection connection = MySQLConnection.getConnection()) {
            String deleteSql = "DELETE FROM Requests WHERE request_id = ?";

            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                // Xóa request khỏi bảng Requests
                deleteStmt.setInt(1, request.getRequestId());
                deleteStmt.executeUpdate();

                // Xóa dòng khỏi TableView
                requestList.remove(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to reject request.");
        }
    }

    // Hiển thị thông báo lỗi
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
