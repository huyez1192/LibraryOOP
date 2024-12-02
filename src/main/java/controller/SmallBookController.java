package controller;

import Objects.Document;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class SmallBookController {

    @FXML
    private ImageView bookImage;  // Để hiển thị ảnh bìa sách

    @FXML
    private Label titleLabel;     // Để hiển thị tên sách

    @FXML
    private Label authorLabel;    // Để hiển thị tác giả sách

    // Phương thức này dùng để load thông tin sách vào các thành phần trong FXML
    public void loadBookInfo(Document document) {
        titleLabel.setText(document.getTitle());  // Hiển thị tên sách
        authorLabel.setText(document.getAuthors());  // Hiển thị tác giả sách

        // Nếu có ảnh bìa, hiển thị
        if (document.getThumbnailLink() != null) {
            bookImage.setImage(new javafx.scene.image.Image(document.getThumbnailLink()));
        }
    }
}
