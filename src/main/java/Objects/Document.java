package Objects;

import com.mysql.cj.conf.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Document {
    private String isbn;
    private String title;
    private String authors;
    private String description;
    private String categories;
    private String thumbnailLink;
    private String previewLink;
    private int quantity;
    private boolean isBorrowed;

    public Document(String isbn) {
        this.isbn = isbn;
    }

    public Document(String isbn, String title, String authors, String description, String categories, String thumbnailLink, String previewLink, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.categories = categories;
        this.thumbnailLink = thumbnailLink;
        this.previewLink = previewLink;
        this.quantity = quantity;
        this.isBorrowed = false;
    }

    public Document(String isbn, String title, String authors,String categories,String description,  String thumbnailLink) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.categories = categories;
        this.thumbnailLink = thumbnailLink;
        this.isBorrowed = false;
    }

    // Getters and setters
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void borrowBook() {
        this.isBorrowed = true;
    }

    public void returnBook() {
        this.isBorrowed = false;
    }
}

