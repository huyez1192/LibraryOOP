package dao;

import Objects.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    private Connection connection;

    public CommentDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean addComment(Comment comment) {
        String sql = "INSERT INTO comments (user_id, isbn, comment, rating) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, comment.getUserId());
            pstmt.setString(2, comment.getIsbn());
            pstmt.setString(3, comment.getCommentText());
            pstmt.setInt(4, comment.getRating());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Comment> getCommentsByIsbn(String isbn) {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT c.comment, c.rating, u.user_name " +
                "FROM comments c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "WHERE c.isbn = ? " +
                "ORDER BY c.created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String userName = rs.getString("user_name");
                String commentText = rs.getString("comment");
                int rating = rs.getInt("rating");
                Comment comment = new Comment(userName, commentText, rating);
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public double getAverageRating(String isbn) {
        String sql = "SELECT AVG(rating) AS average_rating FROM comments WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("average_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Trả về 0 nếu không có đánh giá
    }

}
