// để xử lý các thao tác với cơ sở dữ liệu như thêm, đọc, cập nhật và xóa dữ liệu.
package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/libraryy";
    private static final String USER = "root";
    private static final String PASSWORD = "huyen16125";

    public void addBook(Book book) {
        String sql = "INSERT INTO books (id, title, authors, description, " +
                "categories, " +
                "thumbnail_link) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, book.getId());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthors());
            statement.setString(4, book.getDescription());
            statement.setString(5, book.getCategories());
            statement.setString(6, book.getThumbnailLink());

            statement.executeUpdate();
            System.out.println("Thêm sách thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm sách.");
            e.printStackTrace();
        }
    }
}
