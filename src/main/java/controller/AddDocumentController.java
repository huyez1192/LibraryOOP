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
    private void searchDocuments(ActionEvent event) throws IOException {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            System.out.println("Search query is empty.");
            return;
        }

        HttpURLConnection connection = null;
        try {
            // Mã hóa URL để tránh lỗi ký tự đặc biệt
            String encodedQuery = URLEncoder.encode("intitle:" + query, StandardCharsets.UTF_8);
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedQuery;

            // Gửi yêu cầu HTTP đến API và xử lý kết quả
            connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
            documentsList.clear(); // Xóa dữ liệu cũ trước khi cập nhật kết quả mới

            if (responseJson.has("items")) {
                JsonArray items = responseJson.getAsJsonArray("items");
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "N/A";
                    String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "N/A";
                    String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : "N/A";
                    String categories = volumeInfo.has("categories") ? volumeInfo.getAsJsonArray("categories").get(0).getAsString() : "N/A";
                    String thumbnailLink = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                            ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "N/A";
                    String previewLink = volumeInfo.has("previewLink") ? volumeInfo.get("previewLink").getAsString() : "N/A";
                    int quantity = volumeInfo.has("quantity") ? volumeInfo.get("quantity").getAsInt() : 0;

                    Document document = new Document(
                            volumeInfo.has("industryIdentifiers") ? volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "N/A",
                            title,
                            authors,
                            description,
                            categories,
                            thumbnailLink,
                            previewLink,
                            quantity
                    );

                    documentsList.add(document);
                }
            } else {
                System.out.println("No documents found.");
            }

            // Cập nhật dữ liệu vào TableView
            documentTableViewTable.setItems(documentsList);
        } catch (IOException e) {
            System.err.println("Error fetching data from API: " + e.getMessage());
            e.printStackTrace();
        }
        int responseCode = connection.getResponseCode();
        System.out.println("API Response Code: " + responseCode);
        if (responseCode != 200) {
            System.err.println("Failed to fetch data from API. Response Code: " + responseCode);
            return;
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

