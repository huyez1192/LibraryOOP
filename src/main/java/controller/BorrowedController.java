package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class BorrowedController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> titleColumn;

    @FXML
    private TableColumn<?, ?> isbnColumn;

    @FXML
    private TableColumn<?, ?> borrowDateColumn;

    // Initialize method is called automatically after the FXML is loaded
    @FXML
    public void initialize() {
        // Set up the columns if needed (e.g., setCellValueFactory for dynamic data)
        setupTableColumns();
    }

    private void setupTableColumns() {
        // Example for setting up cell factories (modify according to your data model)
        // titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        // isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        // borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
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
}
