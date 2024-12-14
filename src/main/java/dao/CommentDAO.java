package dao;

import Objects.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private final Connection connection;

    public CommentDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Comment> getCommentsByIsbn(String isbn) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.comment, u.user_name FROM comments c JOIN users u ON c.user_id = u.user_id WHERE c.isbn = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String commentText = rs.getString("comment");
                String userName = rs.getString("user_name");
                comments.add(new Comment(userName, commentText));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return comments;
    }

    public boolean addComment(Comment comment) {
        String sql = "INSERT INTO comments (user_id, isbn, comment) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, comment.getUserId());
            pstmt.setString(2, comment.getIsbn());
            pstmt.setString(3, comment.getCommentText());
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
