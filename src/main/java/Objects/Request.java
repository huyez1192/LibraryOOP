package Objects;

import java.util.Date;

public class Request {
    private int requestId;
    private int userId;
    private String isbn;
    private Date requestDate;

    private String userName;
    private String title;
    private String authors;
    private String response;

    // Constructors, getters and setters
    public Request() {
    }

    public Request(int userId, String isbn, Date requestDate) {
        this.userId = userId;
        this.isbn = isbn;
        this.requestDate = requestDate;
    }

    public Request(int requestId, int userId, String isbn, Date requestDate) {
        this.requestId = requestId;
        this.userId = userId;
        this.isbn = isbn;
        this.requestDate = requestDate;
    }


    public Request(int requestId, Integer userId, String userName, String isbn, java.sql.Date requestDate) {
        this.requestId = requestId;
        this.userId = userId;
        this.userName = userName;
        this.isbn = isbn;
        this.requestDate = requestDate;
    }

    public Request(int requestId, String isbn, String title, String authors, String response) {
        this.requestId = requestId;
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.response = response;
    }

    public Request(String isbn, String title, String authors, String response) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.response = response;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getAuthors() {
        return authors;
    }
}