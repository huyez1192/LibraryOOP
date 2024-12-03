package Objects;
import java.time.LocalDate;
import java.util.Date;


public class BorrowRecord {
    private int borrowId;
    private int userId;
    private String documentTitle;
    private String isbn;
    private Date borrowDate;
    private Date returnDate;
    private String status;

    // Constructors, getters and setters
    public BorrowRecord() {
    }

    public BorrowRecord(int user_id, String isbn, Date returnDate) {
        this.userId = user_id;
        this.isbn = isbn;
        this.returnDate = returnDate;
    }

    public BorrowRecord(String documentTitle, String isbn, Date borrowDate, Date returnDate) {
        this.documentTitle = documentTitle;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public BorrowRecord(int borrowId, int userId, String isbn, Date borrowDate, Date returnDate, String status) {
        this.borrowId = borrowId;
        this.userId = userId;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public java.sql.Date getBorrowDate() {
        return (java.sql.Date) borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public java.sql.Date getReturnDate() {
        return (java.sql.Date) returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}