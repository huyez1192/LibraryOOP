package Objects;

public class Comment {
    private int userId;
    private String userName;
    private String isbn;
    private String commentText;

    public Comment(int userId, String isbn, String commentText) {
        this.userId = userId;
        this.isbn = isbn;
        this.commentText = commentText;
    }

    public Comment(String userName, String commentText) {
        this.userName = userName;
        this.commentText = commentText;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCommentText() {
        return commentText;
    }
}
