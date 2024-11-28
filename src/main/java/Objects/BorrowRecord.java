package Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class BorrowRecord {
    private final StringProperty borrowerName;
    private final StringProperty borrowerID;
    private final StringProperty documentTitle;
    private final StringProperty isbn;
    private final StringProperty borrowDate;
    private final StringProperty returnDate;

    public BorrowRecord(String borrowerName, String borrowerID, String documentTitle, String bookId, LocalDate borrowDate, LocalDate returnDate) {
        this.borrowerName = new SimpleStringProperty(borrowerName);
        this.borrowerID = new SimpleStringProperty(borrowerID);
        this.documentTitle = new SimpleStringProperty(documentTitle);
        this.isbn = new SimpleStringProperty(bookId);
        this.borrowDate = new SimpleStringProperty(borrowDate.toString());
        this.returnDate = new SimpleStringProperty(returnDate.toString());
    }

    public String getBorrowerName() {
        return borrowerName.getName();
    }

    public StringProperty borrowerNameProperty() {
        return borrowerName;
    }

    public String getBorrowerID() {
        return borrowerID.get();
    }

    public StringProperty borrowerIDProperty() {
        return borrowerID;
    }

    public String getDocumentTitle() {
        return documentTitle.get();
    }

    public StringProperty documentTitleProperty() {
        return documentTitle;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public StringProperty isbnProperty() {
        return isbn;
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
