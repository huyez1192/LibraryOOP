package connect;

import Objects.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    public static boolean addDocumentToDatabase(Document document) {
        try {
            Connection connection = null;
            // Đăng ký JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo kết nối với cơ sở dữ liệu
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "INSERT INTO books (isbn, title, authors, description, categories, thumbnail_link, previewLink, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, document.getIsbn());
            stmt.setString(2, document.getTitle());
            stmt.setString(3, document.getAuthors());
            stmt.setString(4, document.getDescription());
            stmt.setString(5, document.getCategories());
            stmt.setString(6, document.getThumbnailLink());
            stmt.setString(7, document.getPreviewLink());
            stmt.setInt(8, document.getQuantity());
            int rowsInserted = stmt.executeUpdate();
            connection.close();
            return rowsInserted > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeDocumentFromDatabase(String isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try {
            Connection connection = null;
            // Đăng ký JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo kết nối với cơ sở dữ liệu
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, isbn);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public static boolean updateDocumentTitleInDatabase(String isbn, String newTitle) {
//        String sql = "UPDATE books SET title = ? WHERE isbn = ?";
//        try {
//            Connection connection = null;
//            // Đăng ký JDBC Driver
//            Class.forName("com.mysql.cj.jdbc.Driver");
//
//            // Tạo kết nối với cơ sở dữ liệu
//            connection = DriverManager.getConnection(URL, USER, PASSWORD);
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//
//            preparedStatement.setString(1, newTitle);
//            preparedStatement.setString(2, isbn);
//            return preparedStatement.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static boolean updateDocumentQuantityInDatabase(String isbn, String newQuantity) {
        String sql = "UPDATE books SET quantity = ? WHERE isbn = ?";
        try {
            Connection connection = null;
            // Đăng ký JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo kết nối với cơ sở dữ liệu
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, newQuantity);
            preparedStatement.setString(2, isbn);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeUserFromDatabase(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try {
            Connection connection = null;
            // Đăng ký JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo kết nối với cơ sở dữ liệu
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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