package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Statistics {
    // Phương thức để lấy kết nối tới cơ sở dữ liệu
    private static Connection getConnection() throws Exception {
        // Cập nhật thông tin kết nối với cơ sở dữ liệu của bạn ở đây
        String url = "jdbc:mysql://localhost:3306/library";
        String user = "root";
        String password = "caohuongiang171";
        return DriverManager.getConnection(url, user, password);
    }

    // Lấy tổng số sách từ bảng Books
    public static int getTotalBooks() {
        int totalBooks = 0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) AS total_books FROM books"; // Truy vấn để lấy tổng số sách
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                totalBooks = rs.getInt("total_books");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalBooks;
    }

    // Lấy tổng số người dùng từ bảng Users
    public static int getTotalUsers() {
        int totalUsers = 0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) AS total_users FROM users"; // Truy vấn để lấy tổng số người dùng
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                totalUsers = rs.getInt("total_users");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalUsers;
    }

    // Lấy tổng số yêu cầu từ bảng Requests
    public static int getTotalRequests() {
        int totalRequests = 0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) AS total_requests FROM requests"; // Truy vấn để lấy tổng số yêu cầu
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                totalRequests = rs.getInt("total_requests");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalRequests;
    }
}
