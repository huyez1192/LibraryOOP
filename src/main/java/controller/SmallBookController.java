package controller;

import Objects.Document;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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

        // Hiển thị tên sách và tác giả
        titleLabel.setText(document.getTitle());
        authorLabel.setText(document.getAuthors());

        // Kiểm tra nếu có ảnh bìa và tải ảnh từ URL
        if (document.getThumbnailLink() != null) {
            try {
                // Nếu đường dẫn là URL hợp lệ, sử dụng nó để tải ảnh
                Image image = new Image(document.getThumbnailLink());
                bookImage.setImage(image);
            } catch (IllegalArgumentException e) {
                // Nếu URL không hợp lệ, in ra lỗi
                System.out.println("Invalid image URL: " + document.getThumbnailLink());
            }
        } else {
            System.out.println("No thumbnail link provided for the book.");
        }
    }
}
