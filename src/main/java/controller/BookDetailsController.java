package controller;

import java.sql.Date;

import Objects.BorrowRecord;
import Objects.Document;
import dao.BorrowRecordDAO;
import dao.RequestDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;

public class BookDetailsController {

    private RequestDAO requestDAO;  // Quản lý yêu cầu mượn sách
    private int userId;  // ID người dùng
    private Document document;  // Thông tin cuốn sách
    private BorrowRecordDAO borrowRecordDAO;

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

    // Phương thức loadBookDetails để hiển thị thông tin sách

    public void loadBookDetails(Document document, int userId, RequestDAO requestDAO, BorrowRecordDAO borrowRecordDAO) {
        this.document = document;
        this.userId = userId;
        this.requestDAO = requestDAO;
        this.borrowRecordDAO = borrowRecordDAO;

        // Cập nhật thông tin sách vào giao diện
        bookTitle.setText(document.getTitle());
        bookAuthors.setText("Tác giả: " + document.getAuthors());
        bookCategory.setText("Thể loại: " + document.getCategories());
        bookDescription.setText(document.getDescription());

        // Cập nhật hình ảnh bìa sách
        if (document.getThumbnailLink() != null) {
            bookThumbnail.setImage(new Image(document.getThumbnailLink()));
        } else {
            bookThumbnail.setImage(new Image(getClass().getResourceAsStream("/images/default_book.png")));
        }

        // Kiểm tra trạng thái yêu cầu mượn sách
        checkRequestStatus();
    }


    // Kiểm tra trạng thái yêu cầu mượn sách
    private void checkRequestStatus() {
        boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());
        boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());

        if (borrowedExists) {
            borrowButton.setText("Đã mượn");
            borrowButton.setDisable(true);
        } else if (requestExists) {
            borrowButton.setText("Chờ phê duyệt");
            borrowButton.setDisable(true);
        } else {
            borrowButton.setText("Mượn sách");
            borrowButton.setDisable(false);
        }
    }

    @FXML
    private void handleBorrowBook() {
        if (requestDAO != null && borrowRecordDAO != null && document != null) {
            boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());
            boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());

            if (!requestExists && !borrowedExists) {
                // Gửi yêu cầu mượn sách
                Date requestDate = new Date(System.currentTimeMillis());
                requestDAO.addRequest(userId, document.getIsbn(), requestDate);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Yêu cầu mượn sách");
                alert.setHeaderText(null);
                alert.setContentText("Yêu cầu mượn sách đã được gửi. Chờ phê duyệt.");
                alert.showAndWait();

                // Cập nhật trạng thái nút
                checkRequestStatus();
            } else if (requestExists && !borrowedExists) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Yêu cầu đã tồn tại");
                alert.setHeaderText(null);
                alert.setContentText("Bạn đã yêu cầu mượn sách này rồi.");
                alert.showAndWait();
            } else if (borrowedExists) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sách đã được mượn");
                alert.setHeaderText(null);
                alert.setContentText("Bạn đã mượn sách này.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleShareBook() {
        shareMessage.setVisible(true);
    }

    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
}
