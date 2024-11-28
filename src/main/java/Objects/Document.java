package Objects;

public class Document {
    private String isbn;
    private String title;
    private String description;
    private String thumbnailLink;
    private String previewLink;
    private String authors;
    private String categories;
    private String publisher;

    // Constructors, getters and setters
    public Document() {
    }

    public Document(String isbn, String title, String authors, String description, String categories, String thumbnailLink, String previewLink) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.categories = categories;
        this.thumbnailLink = thumbnailLink;
        this.previewLink = previewLink;
    }

    public Document(String isbn, String title, String description, String thumbnailLink, String previewLink,
                String authors, String categories, String publisher) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.thumbnailLink = thumbnailLink;
        this.previewLink = previewLink;
        this.authors = authors;
        this.categories = categories;
        this.publisher = publisher;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
