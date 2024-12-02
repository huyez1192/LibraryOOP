package connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/libraryy";
    private static final String USER = "root"; // Thay bằng user của bạn
    private static final String PASSWORD = "huyen16125"; // Thay bằng password của bạn

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Đăng ký JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo kết nối với cơ sở dữ liệu
            connection = DriverManager.getConnection(URL, USER, PASSWORD);


            System.out.println("Connection successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Gọi hàm kết nối
        getConnection();
    }
}