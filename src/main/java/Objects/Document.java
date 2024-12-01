package Objects;

public class Document {
    private String isbn;
    private String title;
    private String authors;
    private String description;
    private String categories;
    private String thumbnailLink;
    private String previewLink;
    private int copiesAvailable;
    private boolean isBorrowed;

    public Document(String isbn, String title, String authors, String description, String categories, String thumbnailLink, String previewLink, int copiesAvailable) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.categories = categories;
        this.thumbnailLink = thumbnailLink;
        this.previewLink = previewLink;
        this.copiesAvailable = copiesAvailable;
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

    public String getAuthor() {
        return authors;
    }

    public void setAuthor(String authors) {
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

    public String getPreviewlLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
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

