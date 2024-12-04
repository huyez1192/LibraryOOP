package controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.Base64;

import Objects.BorrowRecord;
import Objects.Document;
import QRCode.AppUtil;
import dao.BorrowRecordDAO;
import dao.RequestDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private Button closeQrButton;

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
        if (document.getThumbnailLink() != null && !document.getThumbnailLink().isEmpty()) {
            bookThumbnail.setImage(new Image(document.getThumbnailLink()));
        } else {
            bookThumbnail.setImage(new Image(getClass().getResourceAsStream("/images/default_book.png")));
        }

        // Kiểm tra trạng thái yêu cầu mượn sách
        checkRequestStatus();
    }

    // Kiểm tra trạng thái yêu cầu mượn sách
    private void checkRequestStatus() {
        boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());
        boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());

        if (borrowedExists) {
            borrowButton.setText("Trả sách");
            borrowButton.setDisable(false); // Kích hoạt nút để người dùng có thể nhấn
            // Đặt hành động cho nút Trả sách
            borrowButton.setOnAction(event -> handleReturnBook());
        } else if (requestExists) {
            borrowButton.setText("Chờ phê duyệt");
            borrowButton.setDisable(true);
            // Đặt hành động rỗng vì nút bị vô hiệu hóa
            borrowButton.setOnAction(null);
        } else {
            borrowButton.setText("Mượn sách");
            borrowButton.setDisable(false);
            // Đặt hành động cho nút Mượn sách
            borrowButton.setOnAction(event -> handleBorrowBook());
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
    private void handleReturnBook() {
        if (borrowRecordDAO != null && document != null) {
            // Kiểm tra xem người dùng đã mượn sách này chưa
            boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());

            if (borrowedExists) {
                // Xóa bản ghi mượn sách từ cơ sở dữ liệu
                boolean deleteSuccess = borrowRecordDAO.deleteBorrowRecord(userId, document.getIsbn());

                if (deleteSuccess) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Trả sách thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Bạn đã trả sách thành công.");
                    alert.showAndWait();

                    // Cập nhật trạng thái nút
                    checkRequestStatus();
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Lỗi khi trả sách");
                    alert.setHeaderText(null);
                    alert.setContentText("Có lỗi xảy ra khi trả sách. Vui lòng thử lại.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Chưa mượn sách");
                alert.setHeaderText(null);
                alert.setContentText("Bạn chưa mượn sách này.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleShareBook() {
        if (document == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không tìm thấy thông tin sách để chia sẻ.");
            alert.showAndWait();
            return;
        }

        // Dữ liệu cần mã hóa thành QR code
        String qrData = "Sách: " + document.getTitle() + "\nISBN: " + document.getIsbn();

        // Tạo mã QR dưới dạng Base64
        String qrCodeBase64 = AppUtil.generateQrCode(qrData, 300, 300);

        if (qrCodeBase64 == null || qrCodeBase64.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể tạo mã QR.");
            alert.showAndWait();
            return;
        }

        // Loại bỏ phần "data:image/png;base64," nếu có
        String base64Image = qrCodeBase64.contains(",") ? qrCodeBase64.split(",")[1] : qrCodeBase64;

        try {
            // Giải mã Base64 thành mảng byte
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Tạo Image từ mảng byte
            Image qrImage = new Image(new ByteArrayInputStream(imageBytes));

            // Tải FXML của QRCodeView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/QRCodeView.fxml"));
            VBox qrRoot = loader.load();

            // Lấy controller của QRCodeView
            QRCodeViewController qrController = loader.getController();

            // Thiết lập QR code Image
            qrController.setQrCodeImage(qrImage);

            // Tạo Stage mới cho QR code
            Stage qrStage = new Stage();
            qrStage.setTitle("Mã QR của " + document.getTitle());
            qrStage.setScene(new Scene(qrRoot));
            qrStage.initOwner(shareButton.getScene().getWindow()); // Đặt owner cho Stage mới
            qrStage.initModality(Modality.APPLICATION_MODAL); // Ngăn tương tác với cửa sổ chính
            qrStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể hiển thị mã QR.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
}