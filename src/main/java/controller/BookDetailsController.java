package controller;

import Objects.Document;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

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
    private TextArea bookDescription;

    @FXML
    private Button borrowButton;

    @FXML
    private Button shareButton;

    @FXML
    private Button closeButton;

    @FXML
    private Label shareMessage;

    // Phương thức này sẽ được gọi để load thông tin cuốn sách
    public void loadBookDetails(Document document) {
        // Set thông tin sách vào các control
        bookTitle.setText(document.getTitle());
        bookAuthors.setText("Tác giả: " + document.getAuthors());  // Thay getAuthor() thành getAuthors()
        bookCategory.setText("Thể loại: " + document.getCategories());
        bookDescription.setText(document.getDescription());

        // Cập nhật hình ảnh bìa sách
        if (document.getThumbnailLink() != null) {
            bookThumbnail.setImage(new Image(document.getThumbnailLink()));
        }
    }

    // Xử lý nút mượn sách
    @FXML
    private void handleBorrowBook() {
        // Code để xử lý mượn sách
        System.out.println("Sách đã được mượn: " + bookTitle.getText());
    }

    // Xử lý nút chia sẻ sách
    @FXML
    private void handleShareBook() {
        // Code để chia sẻ sách
        shareMessage.setVisible(true);
        System.out.println("Đã chia sẻ đường dẫn của sách.");
    }

    // Xử lý nút đóng
    @FXML
    private void handleClose() {
        // Đóng cửa sổ chi tiết sách
        closeButton.getScene().getWindow().hide();
    }
}
