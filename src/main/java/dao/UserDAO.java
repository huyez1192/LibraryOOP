package dao;

import Objects.User;
import connect.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection connection;

    public UserDAO() {
        this.connection = MySQLConnection.getConnection(); // Tự động lấy Connection
    }

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Thêm người dùng mới
    public void addUser(User user) {
        String query = "INSERT INTO Users (full_name, user_name, pass_word, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getPassword()); // Lưu mật khẩu dưới dạng văn bản thuần
            ps.setString(4, user.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả người dùng
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("user_name"),
                        rs.getString("pass_word"),
                        rs.getString("email")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Tìm người dùng theo user_id
    public User getUserById(int userId) {
        User user = null;
        String query = "SELECT * FROM Users WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("user_name"),
                            rs.getString("pass_word"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // Tìm người dùng theo user_name
    public User getUserByUserName(String userName) {
        User user = null;
        String query = "SELECT * FROM Users WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("user_name"),
                            rs.getString("pass_word"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // Xác thực người dùng (kiểm tra user_name và password)
    public boolean authenticate(String userName, String password) {
        String query = "SELECT pass_word FROM Users WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // So sánh mật khẩu nhập vào với mật khẩu lưu trong cơ sở dữ liệu
                    return password.equals(rs.getString("pass_word")); // So sánh trực tiếp
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin người dùng
    public void updateUser(User user) {
        String query = "UPDATE Users SET full_name = ?, user_name = ?, pass_word = ?, email = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getPassword()); // Lưu mật khẩu dưới dạng văn bản thuần
            ps.setString(4, user.getEmail());
            ps.setInt(5, user.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa người dùng
    public void deleteUser(int userId) {
        String query = "DELETE FROM Users WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra xem người dùng với username và email có tồn tại không.
     * Nếu tồn tại, cập nhật mật khẩu mới và trả về true.
     * Ngược lại, trả về false.
     *
     * @param username    Tên người dùng
     * @param email       Địa chỉ email
     * @param newPassword Mật khẩu mới
     * @return true nếu cập nhật thành công, false nếu không tìm thấy người dùng
     */
    public boolean updatePasswordIfUserExists(String username, String email, String newPassword) {
        String query = "SELECT user_id FROM Users WHERE user_name = ? AND email = ?";
        String updateQuery = "UPDATE Users SET pass_word = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                try (PreparedStatement updatePs = connection.prepareStatement(updateQuery)) {
                    updatePs.setString(1, newPassword); // Lưu mật khẩu dưới dạng văn bản thuần
                    updatePs.setInt(2, userId);
                    updatePs.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
