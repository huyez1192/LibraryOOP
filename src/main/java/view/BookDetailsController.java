package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import Main.Book;

public class BookDetailsController {

    @FXML
    private ImageView bookThumbnail;
    @FXML
    private Label bookTitle;
    @FXML
    private Label bookAuthors;
    @FXML
    private Label bookCategory;
    @FXML
    private Label bookDescription;

    // Phương thức để truyền dữ liệu sách vào giao diện
    public void setBookDetails(Book book) {
        bookTitle.setText(book.getTitle());
        bookAuthors.setText("Author(s): " + book.getAuthors());
        bookCategory.setText("Category: " + book.getCategories());
        bookDescription.setText(book.getDescription());

        // Thiết lập ảnh bìa của sách nếu có link
        if (book.getThumbnailLink() != null && !book.getThumbnailLink().isEmpty()) {
            Image image = new Image(book.getThumbnailLink(), true);
            bookThumbnail.setImage(image);
        }
    }

    // Phương thức để đóng cửa sổ
    @FXML
    private void handleClose() {
        Stage stage = (Stage) bookThumbnail.getScene().getWindow();
        stage.close();
    }
}
