package BorrowedBooksList;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import Objects.BorrowRecord;
import Objects.Library;
public class BorrowedBooksController {

    @FXML
    private TableView<BorrowRecord> borrowedBooksTable;

    @FXML
    private TableColumn<BorrowRecord, String> bookTitleColumn;

    @FXML
    private TableColumn<BorrowRecord, String> bookIdColumn;

    @FXML
    private TableColumn<BorrowRecord, String> borrowDateColumn;

    @FXML
    private TableColumn<BorrowRecord, String> returnDateColumn;

    private Library library;

    @FXML
    public void initialize() {
        // Khởi tạo thư viện
        library = new Library();

        // Thiết lập các cột cho bảng với các thuộc tính của BorrowRecord
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        // Thiết lập dữ liệu cho bảng từ borrowList của Library
        borrowedBooksTable.setItems(library.getBorrowList());

    }
}

