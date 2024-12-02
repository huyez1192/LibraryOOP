package controller;

import java.sql.Date;
import java.sql.SQLException;
import dao.RequestDAO;
import Objects.Document;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;

public class BookDetailsController {

    private RequestDAO requestDAO;  // Đối tượng RequestDAO để quản lý yêu cầu mượn sách
    private int userId;  // ID người dùng
    private Document document;  // Đối tượng Document lưu thông tin cuốn sách

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

    // Phương thức loadBookDetails để hiển thị thông tin cuốn sách
    public void loadBookDetails(Document document, int userId, RequestDAO requestDAO) {
        this.document = document;  // Lưu đối tượng Document
        this.userId = userId;      // Lưu userId
        this.requestDAO = requestDAO; // Lưu RequestDAO để kiểm tra yêu cầu mượn sách

        // Cập nhật thông tin sách vào các control
        bookTitle.setText(document.getTitle());
        bookAuthors.setText("Tác giả: " + document.getAuthors());
        bookCategory.setText("Thể loại: " + document.getCategories());
        bookDescription.setText(document.getDescription());

        // Cập nhật hình ảnh bìa sách nếu có
        if (document.getThumbnailLink() != null) {
            bookThumbnail.setImage(new Image(document.getThumbnailLink()));
        }

        // Kiểm tra trạng thái yêu cầu mượn sách
        checkRequestStatus();
    }

    // Phương thức kiểm tra trạng thái yêu cầu mượn sách
    private void checkRequestStatus() {
        // Kiểm tra xem người dùng đã yêu cầu mượn sách này chưa và trạng thái của yêu cầu
        boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());
        if (requestExists) {
            // Nếu yêu cầu đã tồn tại, thay đổi nút thành "Chờ phê duyệt"
            borrowButton.setText("Chờ phê duyệt");
            borrowButton.setDisable(true); // Disable nút "Mượn sách" khi đang chờ phê duyệt
        } else {
            // Nếu chưa yêu cầu, giữ nút "Mượn sách"
            borrowButton.setText("Mượn sách");
            borrowButton.setDisable(false); // Enable lại nút "Mượn sách"
        }
    }

    @FXML
    private void handleBorrowBook() {
        try {
            if (requestDAO != null && document != null) {
                // Kiểm tra xem người dùng đã yêu cầu mượn sách này chưa
                boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());
                if (!requestExists) {
                    // Nếu chưa có yêu cầu, tiến hành thêm yêu cầu mượn sách
                    borrowButton.setText("Chờ phê duyệt");

                    // Thêm yêu cầu mượn sách vào cơ sở dữ liệu
                    Date requestDate = new Date(System.currentTimeMillis());
                    requestDAO.addRequest(userId, document.getIsbn(), requestDate);

                    // Hiển thị thông báo
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Yêu cầu mượn sách");
                    alert.setHeaderText(null);
                    alert.setContentText("Yêu cầu mượn sách đã được gửi. Chờ phê duyệt.");
                    alert.showAndWait();

                    System.out.println("Đã mượn sách: " + document.getTitle());

                    // Sau khi gửi yêu cầu, kiểm tra lại trạng thái
                    checkRequestStatus();
                } else {
                    // Nếu yêu cầu đã tồn tại, thông báo cho người dùng
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Yêu cầu đã tồn tại");
                    alert.setHeaderText(null);
                    alert.setContentText("Bạn đã yêu cầu mượn sách này rồi.");
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Lỗi hệ thống");
            alert.setHeaderText(null);
            alert.setContentText("Đã xảy ra lỗi khi gửi yêu cầu mượn sách. Vui lòng thử lại sau.");
            alert.showAndWait();
        }
    }

    // Phương thức xử lý khi người dùng bấm nút chia sẻ sách
    @FXML
    private void handleShareBook() {
        // Code để chia sẻ sách
        shareMessage.setVisible(true);
        System.out.println("Đã chia sẻ đường dẫn của sách.");
    }

    // Phương thức đóng cửa sổ chi tiết sách
    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
}
