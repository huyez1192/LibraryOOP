package Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class BorrowRecord {
    private final StringProperty bookTitle;
    private final StringProperty bookId;
    private final StringProperty borrowDate;
    private final StringProperty returnDate;

    public BorrowRecord(String bookTitle, String bookId, LocalDate borrowDate, LocalDate returnDate) {
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.bookId = new SimpleStringProperty(bookId);
        this.borrowDate = new SimpleStringProperty(borrowDate.toString());
        this.returnDate = new SimpleStringProperty(returnDate.toString());
    }

    public String getBookTitle() {
        return bookTitle.get();
    }

    public StringProperty bookTitleProperty() {
        return bookTitle;
    }

    public String getBookId() {
        return bookId.get();
    }

    public StringProperty bookIdProperty() {
        return bookId;
    }

    public String getBorrowDate() {
        return borrowDate.get();
    }

    public StringProperty borrowDateProperty() {
        return borrowDate;
    }

    public String getReturnDate() {
        return returnDate.get();
    }

    public StringProperty returnDateProperty() {
        return returnDate;
    }
}
