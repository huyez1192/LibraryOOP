package controller;

import dao.BorrowRecordDAO;
import Objects.BorrowRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class BorrowHistoryController {

    @FXML
    private TableView<BorrowRecord> borrowHistoryTable;

    @FXML
    private TableColumn<BorrowRecord, String> documentTitleColumn;

    @FXML
    private TableColumn<BorrowRecord, String> borrowDateColumn;

    @FXML
    private TableColumn<BorrowRecord, String> returnDateColumn;

    private int userId; // Biến lưu trữ userId

    private BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO(); // Khởi tạo BorrowRecordDAO

    /**
     * Thiết lập userId và tải lịch sử mượn sách.
     *
     * @param userId ID của người dùng
     */
    public void setUserId(int userId) {
        this.userId = userId;
        loadBorrowHistory();
    }

    /**
     * Tải lịch sử mượn sách từ BorrowRecordDAO và hiển thị trong TableView.
     */
    private void loadBorrowHistory() {
        List<BorrowRecord> borrowHistory = borrowRecordDAO.getBorrowHistoryByUserId(userId);
        ObservableList<BorrowRecord> borrowHistoryList = FXCollections.observableArrayList(borrowHistory);
        borrowHistoryTable.setItems(borrowHistoryList);
    }

    /**
     * Cài đặt các cột trong TableView.
     */
    @FXML
    public void initialize() {
        // Liên kết các cột với thuộc tính trong BorrowRecord
        documentTitleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDocumentTitle()));
        borrowDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBorrowDate().toString()));
        returnDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReturnDate().toString()));
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Close".
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) borrowHistoryTable.getScene().getWindow();
        stage.close();
    }
}
