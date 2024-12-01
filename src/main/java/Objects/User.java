package Objects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String personId;
    private String mail;
    private String password;
    private List<BorrowRecord> borrowRecords; // Danh sách bản ghi mượn

    public User(String name, String personId, String mail, String password) {
        this.name = name;
        this.personId = personId;
        this.mail = mail;
        this.password = password;
        this.borrowRecords = new ArrayList<>(); // Khởi tạo danh sách bản ghi
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    // Thêm bản ghi mượn mới
    public void addBorrowRecord(String documentTitle, String isbn, LocalDate borrowDate, LocalDate returnDate) {
        BorrowRecord record = new BorrowRecord(name, personId, documentTitle, isbn, borrowDate, returnDate);
        borrowRecords.add(record);
    }

    // Xóa bản ghi mượn (theo ISBN)
    public boolean removeBorrowRecord(String isbn) {
        return borrowRecords.removeIf(record -> record.getIsbn().equals(isbn));
    }

    // Hiển thị danh sách các bản ghi mượn
    public void showBorrowRecords() {
        if (borrowRecords.isEmpty()) {
            System.out.println(name + " has not borrowed any documents.");
        } else {
            System.out.println(name + "'s borrowed documents:");
            for (BorrowRecord record : borrowRecords) {
                System.out.printf("Title: %s, ISBN: %s, Borrowed: %s, Return: %s%n",
                        record.getDocumentTitle(), record.getIsbn(), record.getBorrowDate(), record.getReturnDate());
            }
        }
    }
}


