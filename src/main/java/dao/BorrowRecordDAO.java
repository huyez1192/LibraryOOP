package dao;
import Objects.BorrowRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {
    private Connection connection;

    public BorrowRecordDAO() {
    }

    public BorrowRecordDAO(Connection connection) {
        this.connection = connection;
    }

    public void add(BorrowRecord br) {
        String query = "INSERT INTO BorrowedBooks (user_id, isbn, borrow_date, return_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, br.getUserId());
            ps.setString(2, br.getIsbn());
            ps.setDate(3, br.getBorrowDate());
            ps.setDate(4, br.getReturnDate());
            ps.setString(5, br.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BorrowRecord> getAllRecord() {
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String query = "SELECT * FROM BorrowedBooks";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BorrowRecord br = new BorrowRecord();
                br.setBorrowId(rs.getInt("borrow_id"));
                br.setUserId(rs.getInt("user_id"));
                br.setIsbn(rs.getString("isbn"));
                br.setBorrowDate(rs.getDate("borrow_date"));
                br.setReturnDate(rs.getDate("return_date"));
                br.setStatus(rs.getString("status"));
                borrowRecords.add(br);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowRecords;
    }

    public void returnBook(BorrowRecord borrowRecord) {
        // Truy vấn cập nhật trạng thái và ngày trả sách
        String query = "UPDATE BorrowedBooks SET status = 'returned', return_date = ? WHERE borrow_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            // Lấy ngày hiện tại làm ngày trả
            Date returnDate = new Date(System.currentTimeMillis());
            Date borrowDate = borrowRecord.getBorrowDate();

            // Thiết lập tham số cho truy vấn
            ps.setDate(1, returnDate); // Gán ngày trả
            ps.setInt(2, borrowRecord.getBorrowId()); // Gán ID mượn sách
            ps.setDate(3, borrowDate);

            // Thực thi truy vấn
            ps.executeUpdate();

            // Cập nhật lại số lượng sách trong kho
            updateBookQuantity(borrowRecord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteRecord(BorrowRecord borrowRecord) {
        String query = "DELETE FROM BorrowedBooks WHERE borrow_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, borrowRecord.getBorrowId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBookQuantity(BorrowRecord borrowRecord) {
        String query = "UPDATE Books SET quantity = quantity + 1 WHERE isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, borrowRecord.getIsbn());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfBorrowedExists(int userId, String isbn) {
        String query = "SELECT COUNT(*) FROM borrowedbooks WHERE user_id = ? AND isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Nếu có ít nhất một yêu cầu trùng thì trả về true
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
