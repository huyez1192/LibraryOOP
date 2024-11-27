package Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class Library {
    private ObservableList<BorrowRecord> borrowList;

    public Library() {
        this.borrowList = FXCollections.observableArrayList();
    }

    public ObservableList<BorrowRecord> getBorrowList() {
        return borrowList;
    }

    // Thêm một bản ghi mượn sách
    public void addBorrowRecord(String borrowerName, String borrowerID, String bookTitle, String bookId, LocalDate borrowDate, LocalDate returnDate) {
        BorrowRecord record = new BorrowRecord(borrowerName, borrowerID, bookTitle, bookId, borrowDate, returnDate);
        borrowList.add(record);
    }

    // Xóa một bản ghi mượn sách
    public void removeBorrowRecord(BorrowRecord record) {
        borrowList.remove(record);
    }
}
