package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    // Hàm khởi tạo (initialize) để load dữ liệu mặc định
    @FXML
    public void initialize() {
        // Đặt hình ảnh cho bìa sách
        Image thumbnail = new Image("file:///C:/Users/ASUS/Pictures/bookCover.jpg"); // Đường dẫn ảnh
        bookThumbnail.setImage(thumbnail);

        // Gán dữ liệu ví dụ
        bookTitle.setText("Kỹ Thuật Bào Chế Pellet");
        bookAuthors.setText("Tác giả: Nguyễn Văn A");
        bookCategory.setText("Thể loại: Dược học");
        bookDescription.setText("Đây là một giáo trình đào tạo sau đại học về kỹ thuật bào chế pellet, cung cấp thông tin chi tiết về các phương pháp bào chế hiện đại...");
    }

    // Xử lý khi nhấn "Mượn Sách"
    @FXML
    private void handleBorrowBook() {
        System.out.println("Đã mượn sách!");
    }

    // Xử lý khi nhấn "Chia Sẻ"
    @FXML
    private void handleShareBook() {
        System.out.println("Chia sẻ sách...");
    }

    // Xử lý khi nhấn "Đóng"
    @FXML
    private void handleClose() {
        System.out.println("Đóng giao diện.");
        // Logic đóng cửa sổ, ví dụ: Platform.exit();
    }
}
