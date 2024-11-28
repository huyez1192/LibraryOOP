package dao;

import Objects.Fine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {

    private Connection connection;

    public FineDAO(Connection connection) {
        this.connection = connection;
    }

    // Thêm một bản ghi phạt mới vào cơ sở dữ liệu
    public void add(Fine fine) {
        String query = "INSERT INTO Fines (user_id, borrow_id, fine_amount, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, fine.getUserId());
            ps.setInt(2, fine.getBorrowId());
            ps.setDouble(3, fine.getFineAmount());
            ps.setDate(4, new java.sql.Date(fine.getDueDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả các bản ghi phạt từ cơ sở dữ liệu
    public List<Fine> getAllFines() {
        List<Fine> fines = new ArrayList<>();
        String query = "SELECT * FROM Fines";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Fine fine = new Fine(
                        rs.getInt("fine_id"),
                        rs.getInt("user_id"),
                        rs.getInt("borrow_id"),
                        rs.getDouble("fine_amount"),
                        rs.getDate("due_date")
                );
                fines.add(fine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    // Lấy tất cả các bản ghi phạt của một người dùng theo user_id
    public List<Fine> getFinesByUserId(int userId) {
        List<Fine> fines = new ArrayList<>();
        String query = "SELECT * FROM Fines WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fine fine = new Fine(
                            rs.getInt("fine_id"),
                            rs.getInt("user_id"),
                            rs.getInt("borrow_id"),
                            rs.getDouble("fine_amount"),
                            rs.getDate("due_date")
                    );
                    fines.add(fine);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    // Cập nhật số tiền phạt cho một bản ghi phạt
    public void updateFineAmount(int fineId, double fineAmount) {
        String query = "UPDATE Fines SET fine_amount = ? WHERE fine_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, fineAmount);
            ps.setInt(2, fineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa một bản ghi phạt khỏi cơ sở dữ liệu
    public void deleteFine(int fineId) {
        String query = "DELETE FROM Fines WHERE fine_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, fineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    // Kiểm tra xem người dùng có bị phạt hay không
//    public boolean checkIfUserHasFine(int userId) {
//        String query = "SELECT COUNT(*) FROM Fines WHERE user_id = ?";
//        try (PreparedStatement ps = connection.prepareStatement(query)) {
//            ps.setInt(1, userId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1) > 0;  // Nếu có bất kỳ phạt nào thì trả về true
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
