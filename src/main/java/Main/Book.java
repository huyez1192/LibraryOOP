// để lưu trữ dữ liệu sách, đảm bảo nó có các thuộc tính giống với các cột trong bảng books

package Main;

public class Book {
    private String id;
    private String title;
    private String authors;
    private String description;
    private String categories;
    private String thumbnailLink;

    // Constructor, getters và setters cho các thuộc tính trên

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
