package Objects;

//public class Book {
//    private String title;
//    private String author;
//    private String bookId;
//    private boolean isBorrowed;
//
//    public Book(String title, String author, String isbn) {
//        this.title = title;
//        this.author = author;
//        this.bookId = isbn;
//        this.isBorrowed = false;
//    }
//
//    // Getters and setters
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getAuthor() {
//        return author;
//    }
//
//    public void setAuthor(String author) {
//        this.author = author;
//    }
//
//    public String getBookId() {
//        return bookId;
//    }
//
//    public void setBookId(String bookId) {
//        this.bookId = bookId;
//    }
//
//    public boolean isBorrowed() {
//        return isBorrowed;
//    }
//
//    public void borrowBook() {
//        this.isBorrowed = true;
//    }
//
//    public void returnBook() {
//        this.isBorrowed = false;
//    }
//}
//
//// để lưu trữ dữ liệu sách, đảm bảo nó có các thuộc tính giống với các cột trong bảng books
//
////package Main;
////
public class Book {
    private String isbn;
    private String title;
    private String authors;
    private String description;
    private String categories;
    private String thumbnailLink;
    private String previewLink;

    // Constructor, getters và setters cho các thuộc tính trên
    public Book(String isbn, String title, String authors, String description, String categories, String thumbnailLink, String previewLink) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.categories = categories;
        this.thumbnailLink = thumbnailLink;
        this.previewLink = previewLink;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }
}
