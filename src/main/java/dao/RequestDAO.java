package dao;

import Objects.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    private Connection connection;

    public RequestDAO() {
        try {
            // Tạo kết nối với cơ sở dữ liệu (thay thế với thông tin kết nối của bạn)
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library", "root", "gem07012005"); // Cập nhật thông tin kết nối
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Lỗi nếu kết nối không thành công
        }
    }
    public RequestDAO(Connection connection) {
        this.connection = connection;
    }

    // Thêm một yêu cầu mới
    // Thêm một yêu cầu mượn sách vào cơ sở dữ liệu
    public void addRequest(int userId, String isbn, Date requestDate) throws SQLException {
        if (checkIfUserExists(userId)) {
            // Tiến hành thêm yêu cầu
            String query = "INSERT INTO requests (user_id, isbn, request_date) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, userId);
                ps.setString(2, isbn);
                ps.setDate(3, requestDate);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Yêu cầu mượn sách đã được thêm thành công.");
                }
            }
        } else {
            System.out.println("Người dùng không tồn tại.");
        }

    }

    // Lấy tất cả các yêu cầu
    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT * FROM Requests";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Request request = new Request(
                        rs.getInt("request_id"),
                        rs.getInt("user_id"),
                        rs.getString("isbn"),
                        rs.getDate("request_date")
                );
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Lấy tất cả yêu cầu theo user_id
    public List<Request> getRequestsByUserId(int userId) {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT * FROM Requests WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request request = new Request(
                            rs.getInt("request_id"),
                            rs.getInt("user_id"),
                            rs.getString("isbn"),
                            rs.getDate("request_date")
                    );
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Xóa một yêu cầu theo request_id
    public void deleteRequest(int requestId) {
        String query = "DELETE FROM Requests WHERE request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xem yêu cầu của người dùng có tồn tại hay không
    public boolean checkIfRequestExists(int userId, String isbn) {
        String query = "SELECT COUNT(*) FROM Requests WHERE user_id = ? AND isbn = ?";
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
    public boolean checkIfUserExists(int userId) {
        String query = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Số người dùng có ID " + userId + ": " + count);
                    return count > 0;  // Nếu có ít nhất một người dùng trùng thì trả về true
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
