package controller;

import Objects.Document;
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

public class LibraryDocumentController implements Initializable {

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button sumLibraryButton;

    @FXML
    private Button sumBackButton;

    @FXML
    private TableView<Document> documentTableViewTable;

    @FXML
    private TableColumn<Document, String> isbnColumn;

    @FXML
    private TableColumn<Document, String> titleColumn;

    @FXML
    private TableColumn<Document, String> authorsColumn;

    @FXML
    private TableColumn<Document, String> descriptionColumn;

    @FXML
    private TableColumn<Document, String> categoriesColumn;

    @FXML
    private TableColumn<Document, String> thumbnailLinkColumn;

    @FXML
    private TableColumn<Document, String> previewLinkColumn;

    @FXML
    private TableColumn<Document, Integer> quantityColumn;

    private ContextMenu contextMenu;

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=java+programming";

    private ObservableList<Document> documentsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadDataFromDatabase();
        System.out.println(documentTableViewTable);
        // Tạo menu ngữ cảnh
        contextMenu = new ContextMenu();
        // Menu item Remove
        MenuItem removeMenuItem = new MenuItem("Remove");
        removeMenuItem.setOnAction(event -> handleRemoveBook());

        // Menu item Update
        MenuItem updateMenuItem = new MenuItem("Update");
        updateMenuItem.setOnAction(event -> handleUpdateQuantityBook());
        contextMenu.getItems().addAll(removeMenuItem, updateMenuItem);

        // Thêm event nhấn chuột phải vào TableView
        documentTableViewTable.setRowFactory(tv -> {
            TableRow<Document> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
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
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoriesColumn.setCellValueFactory(new PropertyValueFactory<>("categories"));
        thumbnailLinkColumn.setCellValueFactory(new PropertyValueFactory<>("thumbnailLink"));
        previewLinkColumn.setCellValueFactory(new PropertyValueFactory<>("previewLink"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
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
    private void switchToAddDocument(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addDocument.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading add document: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void reload(ActionEvent event) {
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

    private void handleRemoveBook() {
        Document selectedBook = documentTableViewTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Remove Confirmation");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to remove this document?");

            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean success = MySQLConnection.removeDocumentFromDatabase(selectedBook.getIsbn());
                if (success) {
                    documentsList.remove(selectedBook); // Xóa sách khỏi danh sách hiển thị
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Document removed successfully!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to remove document.");
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a document to remove.");
            alert.showAndWait();
        }
    }

//    private void handleUpdateTitleBook() {
//        Document selectedBook = documentTableViewTable.getSelectionModel().getSelectedItem();
//        if (selectedBook != null) {
//            TextInputDialog dialog = new TextInputDialog(selectedBook.getTitle());
//            dialog.setTitle("Update Book");
//            dialog.setHeaderText(null);
//            dialog.setContentText("Enter new title for the book:");
//
//            String newTitle = dialog.showAndWait().orElse(null);
//            if (newTitle != null && !newTitle.trim().isEmpty()) {
//                boolean success = DatabaseUtils.updateBookQuantityInDatabase(selectedBook.getIsbn(), newTitle);
//                if (success) {
//                    selectedBook.setTitle(newTitle); // Cập nhật dữ liệu trong danh sách hiển thị
//                    documentTableViewTable.refresh();
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Success");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Book updated successfully!");
//                    alert.showAndWait();
//                } else {
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setTitle("Error");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Failed to update book.");
//                    alert.showAndWait();
//                }
//            }
//        } else {
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Warning");
//            alert.setHeaderText(null);
//            alert.setContentText("Please select a book to update.");
//            alert.showAndWait();
//        }
//    }

private void handleUpdateQuantityBook() {
    Document selectedBook = documentTableViewTable.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedBook.getQuantity()));
        dialog.setTitle("Update Quantity");
        dialog.setHeaderText("Update Quantity for: " + selectedBook.getTitle());
        dialog.setContentText("Enter new quantity:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                // Chuyển đổi từ String sang int
                int newQuantity = Integer.parseInt(input);

                // Kiểm tra nếu số lượng là hợp lệ (không âm)
                if (newQuantity < 0) {
                    throw new NumberFormatException("Quantity must be non-negative.");
                }

                // Cập nhật vào cơ sở dữ liệu
                boolean success = MySQLConnection.updateDocumentQuantityInDatabase(selectedBook.getIsbn(), String.valueOf(newQuantity));
                if (success) {
                    selectedBook.setQuantity(newQuantity); // Cập nhật trong bảng
                    documentTableViewTable.refresh();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Quantity updated successfully!");
                    alert.showAndWait();
                } else {
                    throw new Exception("Database update failed.");
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid non-negative integer.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update quantity: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }
}

    private void loadDataFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/libraryy";
        String username = "root";
        String password = "";

        String query = """
            SELECT *
            FROM books
        """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                String description = rs.getString("description");
                String categories = rs.getString("categories");
                String thumbnailLink = rs.getString("thumbnail_link");
                String previewLink = rs.getString("previewLink");
                int quantity = rs.getInt("quantity");

                documentsList.add(new Document(isbn, title, authors, description, categories, thumbnailLink, previewLink, quantity));
            }

            documentTableViewTable.setItems(documentsList);

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

        documentsList.clear(); // Xóa dữ liệu cũ trước khi tải mới

        try (Connection connection = MySQLConnection.getConnection()) {
            String sql = "SELECT * FROM books WHERE title LIKE ? OR authors LIKE ? OR isbn LIKE ? OR categories LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "%" + query + "%");
                preparedStatement.setString(2, "%" + query + "%");
                preparedStatement.setString(3, "%" + query + "%");
                preparedStatement.setString(4, "%" + query + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String isbn = resultSet.getString("isbn");
                        String title = resultSet.getString("title");
                        String authors = resultSet.getString("authors");
                        String description = resultSet.getString("description");
                        String categories = resultSet.getString("categories");
                        String thumbnailLink = resultSet.getString("thumbnail_link");
                        String previewLink = resultSet.getString("previewLink");
                        int quantity = resultSet.getInt("quantity");

                        Document document = new Document(isbn, title, authors, description, categories, thumbnailLink, previewLink, quantity);
                        documentsList.add(document); // Thêm sách vào danh sách
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
        documentTableViewTable.setItems(documentsList);
    }

}