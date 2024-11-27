package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextArea;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class BookDetailsController {

    @FXML
    private ImageView bookThumbnail;  // Ảnh bìa
    @FXML
    private Label bookTitle;         // Tiêu đề sách
    @FXML
    private Label bookAuthors;       // Tác giả
    @FXML
    private Label bookCategory;      // Thể loại
    @FXML
    private TextArea bookDescription; // Mô tả sách
    @FXML
    private Button borrowButton;     // Nút Mượn Sách
    @FXML
    private Button shareButton;      // Nút Chia Sẻ
    @FXML
    private Button closeButton;      // Nút Đóng
    @FXML
    private Label shareMessage;      // Thông báo "Đã Lưu Đường Dẫn"

    // Phương thức initialize được gọi tự động sau khi FXML đã được nạp xong
    @FXML
    public void initialize() {
        // Thiết lập ảnh bìa sách
        Image image = new Image("file:///C:/Users/ASUS/Pictures/bookCover.jpg"); // Đảm bảo đường dẫn ảnh chính xác
        bookThumbnail.setImage(image);

        // Thiết lập thông tin sách
        bookTitle.setText("Tên Sách");
        bookAuthors.setText("Tác giả: Tên Tác Giả");
        bookCategory.setText("Thể loại: Tiểu Thuyết");
        bookDescription.setText("Đây là mô tả về sách. Nó có thể dài và bao gồm các chi tiết về nội dung sách.");

        // Thiết lập trạng thái ban đầu cho các nút và label
        borrowButton.setText("Mượn Sách");
        shareMessage.setVisible(false); // Ban đầu ẩn thông báo "Đã Lưu Đường Dẫn"

        // Bạn có thể thêm bất kỳ logic khởi tạo nào ở đây
    }

    // Xử lý sự kiện khi nhấn nút "Mượn Sách"
    @FXML
    public void handleBorrowBook() {
        // Thay đổi văn bản của nút "Mượn Sách" thành "Đã Mượn"
        borrowButton.setText("Đã Mượn");
    }

    // Xử lý sự kiện khi nhấn nút "Chia Sẻ"
    @FXML
    public void handleShareBook() {
        // Hiển thị thông báo "Đã Lưu Đường Dẫn"
        shareMessage.setVisible(true);

        // Tạo một PauseTransition để ẩn thông báo sau 3 giây
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> shareMessage.setVisible(false));

        // Bắt đầu hiệu ứng ẩn
        pause.play();
    }

    // Xử lý sự kiện khi nhấn nút "Đóng"
    @FXML
    public void handleClose() {
        // Đóng cửa sổ hoặc thực hiện các hành động khác nếu cần
        System.exit(0);
    }
}
