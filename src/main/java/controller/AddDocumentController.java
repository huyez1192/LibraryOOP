package controller;

import Objects.Document;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import connect.MySQLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.List;
import utils.BookAPIClient;

public class AddDocumentController extends Controller implements Initializable {

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

    private ObservableList<Document> documentsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        System.out.println(documentTableViewTable);
        // Tạo menu ngữ cảnh
        contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(event -> handleAddDocument()); // Xử lý khi nhấn "Add"
        contextMenu.getItems().add(addMenuItem);

        // Thêm event nhấn chuột phải vào TableView
        documentTableViewTable.setRowFactory(tv -> {
            TableRow<Document> row = new TableRow<>();
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
    private void switchToLibraryDocument() {
        switchScene("/fxml/libraryDocument.fxml", documentTableViewTable);
    }

    public void switchToAdminHome(ActionEvent event) {
        switchScene("/fxml/admindashboard.fxml", documentTableViewTable);
    }

    @FXML
    private void switchToAdminRequests() {
        switchScene("/fxml/requestsScreen.fxml", documentTableViewTable);
    }

    @FXML
    private void switchToAdminUsers() {
        switchScene("/fxml/adminUsers.fxml", documentTableViewTable);
    }

    @FXML
    private void searchDocuments(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            System.out.println("Search query is empty.");
            return;
        }

        try {
            // Gọi API thông qua BookAPIClient
            List<Document> fetchedBooks = BookAPIClient.fetchBooks("?q=intitle:" + query);

            // Xóa dữ liệu cũ và thêm dữ liệu mới
            documentsList.clear();
            documentsList.addAll(fetchedBooks);

            // Cập nhật TableView
            documentTableViewTable.setItems(documentsList);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching data from API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAddDocument() {
        Document selectedDocument = documentTableViewTable.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            // Ví dụ: Thêm sách vào cơ sở dữ liệu
            boolean success = MySQLConnection.addDocumentToDatabase(selectedDocument);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Document added successfully!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to add document.");
                alert.showAndWait();
            }
        }
    }


}

