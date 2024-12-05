package dao;

import Objects.BorrowRecord;
import connect.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {
    private Connection connection;

    public BorrowRecordDAO() {
        this.connection = MySQLConnection.getConnection();
    }

    public BorrowRecordDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Lấy lịch sử mượn sách của người dùng dựa trên userId.
     *
     * @param userId ID của người dùng
     * @return List các bản ghi mượn sách
     */
    public List<BorrowRecord> getBorrowHistoryByUserId(int userId) {
        List<BorrowRecord> borrowHistory = new ArrayList<>();
        String query = "SELECT Books.title AS documentTitle, BorrowRecords.isbn, BorrowRecords.borrow_date, BorrowRecords.return_date " +
                "FROM BorrowRecords " +
                "JOIN Books ON BorrowRecords.isbn = Books.isbn " +
                "WHERE BorrowRecords.user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String documentTitle = rs.getString("documentTitle");
                String isbn = rs.getString("isbn");
                Date borrowDate = rs.getDate("borrow_date");
                Date returnDate = rs.getDate("return_date");
                BorrowRecord record = new BorrowRecord(documentTitle, isbn, borrowDate, returnDate);
                borrowHistory.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrowHistory;
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

            // Thiết lập tham số cho truy vấn
            ps.setDate(1, returnDate); // Gán ngày trả
            ps.setInt(2, borrowRecord.getBorrowId()); // Gán ID mượn sách

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

    // Thêm phương thức xóa bản ghi dựa trên user_id và isbn
    public boolean deleteBorrowRecord(int userId, String isbn) {
        String query = "DELETE FROM BorrowedBooks WHERE user_id = ? AND isbn = ? AND status = 'borrowed'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, isbn);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có bản ghi bị xóa
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        String query = "SELECT COUNT(*) FROM BorrowedBooks WHERE user_id = ? AND isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Trả về true nếu có ít nhất một yêu cầu trùng
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
