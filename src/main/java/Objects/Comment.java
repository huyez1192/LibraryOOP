package Objects;

public class Comment {
    private int userId;
    private String userName;
    private String isbn;
    private String commentText;
    private int rating;

    public Comment(int userId, String isbn, String commentText, int rating) {
        this.userId = userId;
        this.isbn = isbn;
        this.commentText = commentText;
        this.rating = rating;
    }

    public Comment(String userName, String commentText, int rating) {
        this.userName = userName;
        this.commentText = commentText;
        this.rating = rating;
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

    public int getRating() {
        return rating;
    }
}