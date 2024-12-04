package controller;

import Objects.Document;
import Objects.User;
import connect.MySQLConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminUsersController implements Initializable {

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button sumLibraryButton;

    @FXML
    private Button sumBackButton;

    @FXML
    private TableView<User> usersTableViewTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> fullNameColumn;

    @FXML
    private TableColumn<User, String> userNameColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    private ContextMenu contextMenu;

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadDataFromDatabase();
        // Tạo menu ngữ cảnh
        contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Remove");
        addMenuItem.setOnAction(event -> handleRemoveUser()); // Xử lý khi nhấn "Add"
        contextMenu.getItems().add(addMenuItem);

        // Thêm event nhấn chuột phải vào TableView
        usersTableViewTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Kiểm tra nếu sự kiện là chuột phải và row không rỗng
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                } else {
                    contextMenu.hide();
                }
            });
            return row;
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    @FXML
    private void switchToAdminHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admindashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading admin dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToAdminDocuments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/libraryDocument.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading library document: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRemoveUser() {
        User selectedUser = usersTableViewTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Remove Confirmation");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to remove this user?");

            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean success = MySQLConnection.removeUserFromDatabase(selectedUser.getUserId());
                if (success) {
                    usersList.remove(selectedUser); // Xóa sách khỏi danh sách hiển thị
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("User removed successfully!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to remove user.");
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select an user to remove.");
            alert.showAndWait();
        }
    }

    private void loadDataFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/libraryy";
        String username = "root";
        String password = "";

        String query = """
                    SELECT *
                    FROM users
                    WHERE user_id != "1";
                """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String fullName = rs.getString("full_name");
                String userName = rs.getString("user_name");
                String userPassword = rs.getString("pass_word");
                String email = rs.getString("email");

                usersList.add(new User(id, fullName, userName, userPassword, email));
            }

            usersTableViewTable.setItems(usersList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        @FXML
        private void searchUsers(ActionEvent event) {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a keyword to search.");
                alert.showAndWait();
                return;
            }

            usersList.clear(); // Xóa dữ liệu cũ trước khi tải mới

            try (Connection connection = MySQLConnection.getConnection()) {
                String sql = "SELECT * FROM users WHERE user_id LIKE ? OR full_name LIKE ? OR user_name LIKE ? OR email LIKE ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, "%" + query + "%");
                    preparedStatement.setString(2, "%" + query + "%");
                    preparedStatement.setString(3, "%" + query + "%");
                    preparedStatement.setString(4, "%" + query + "%");

                    try (ResultSet rs = preparedStatement.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("user_id");
                            String fullName = rs.getString("full_name");
                            String userName = rs.getString("user_name");
                            String userPassword = rs.getString("pass_word");
                            String email = rs.getString("email");

                            User user = new User(id, fullName, userName, userPassword, email);
                            usersList.add(user); // Thêm sách vào danh sách
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
            usersTableViewTable.setItems(usersList);
        }
}