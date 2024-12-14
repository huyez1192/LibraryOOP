package controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Base64;
import java.util.List;

import Objects.BorrowRecord;
import Objects.Document;
import QRCode.AppUtil;
import dao.BorrowRecordDAO;
import dao.CommentDAO;
import dao.RequestDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import connect.MySQLConnection;  // Thêm import MySQLConnection

import Objects.Comment;

public class BookDetailsController {

    private CommentDAO commentDAO;
    private RequestDAO requestDAO;  // Quản lý yêu cầu mượn sách
    private int userId;  // ID người dùng
    private Document document;  // Thông tin cuốn sách
    private BorrowRecordDAO borrowRecordDAO;
    private Connection connection;  // Kết nối cơ sở dữ liệu

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

    @FXML
    private HBox ratingStars;

    @FXML
    private Label ratingLabel;

    @FXML
    private TextArea commentArea;

    @FXML
    private Button submitReviewButton;

    @FXML
    private VBox commentsContainer;

    private int currentRating = 0;

    // Khởi tạo kết nối cơ sở dữ liệu
    public BookDetailsController() {
        this.connection = MySQLConnection.getConnection(); // Khởi tạo kết nối cơ sở dữ liệu
        this.commentDAO = new CommentDAO(MySQLConnection.getConnection());

    }

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

        // Hiển thị số sao và các bình luận
        displayRatingAndComments();

        // Kiểm tra trạng thái yêu cầu mượn sách
        checkRequestStatus();

        loadComments();
    }

    private void loadComments() {
        commentsContainer.getChildren().clear();
        List<Comment> comments = commentDAO.getCommentsByIsbn(document.getIsbn());

        for (Comment comment : comments) {
            Label commentLabel = new Label(comment.getUserName() + ": " + comment.getCommentText());
            commentLabel.setWrapText(true);
            commentsContainer.getChildren().add(commentLabel);
        }
    }
    // Kiểm tra trạng thái yêu cầu mượn sách
    private void checkRequestStatus() {
        boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());
        boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());

        if (borrowedExists) {
            borrowButton.setText("Trả sách");
            borrowButton.setDisable(false);
            borrowButton.setOnAction(event -> handleReturnBook());
        } else if (requestExists) {
            borrowButton.setText("Chờ phê duyệt");
            borrowButton.setDisable(true);
            borrowButton.setOnAction(null);
        } else {
            borrowButton.setText("Mượn sách");
            borrowButton.setDisable(false);
            borrowButton.setOnAction(event -> handleBorrowBook());
        }
    }

    // Xử lý mượn sách
    @FXML
    private void handleBorrowBook() {
        if (requestDAO != null && borrowRecordDAO != null && document != null) {
            boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());
            boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());

            if (!requestExists && !borrowedExists) {
                int quantity = requestDAO.getBookQuantity(document.getIsbn());
                if (quantity <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Không thể mượn sách", "Sách hiện không có sẵn để mượn.");
                    return;
                }
                Date requestDate = new Date(System.currentTimeMillis());
                requestDAO.addRequest(userId, document.getIsbn(), requestDate);
                showAlert(Alert.AlertType.INFORMATION, "Yêu cầu mượn sách", "Yêu cầu mượn sách đã được gửi. Chờ phê duyệt.");
                checkRequestStatus();
            }
        }
    }

    // Xử lý trả sách
    @FXML
    private void handleReturnBook() {
        if (borrowRecordDAO != null && document != null) {
            boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());

            if (borrowedExists) {
                boolean deleteSuccess = borrowRecordDAO.deleteBorrowRecord(userId, document.getIsbn());
                if (deleteSuccess) {
                    borrowRecordDAO.incrementBookQuantity(document.getIsbn());
                    showAlert(Alert.AlertType.INFORMATION, "Trả sách thành công", "Bạn đã trả sách thành công.");
                    checkRequestStatus();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Chưa mượn sách", "Bạn chưa mượn sách này.");
            }
        }
    }

    // Chia sẻ sách qua mã QR
    @FXML
    private void handleShareBook() {
        if (document == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin sách để chia sẻ.");
            return;
        }

        String qrData = "Sách: " + document.getTitle() + "\nISBN: " + document.getIsbn();
        String qrCodeBase64 = AppUtil.generateQrCode(qrData, 300, 300);

        if (qrCodeBase64 != null && !qrCodeBase64.isEmpty()) {
            String base64Image = qrCodeBase64.contains(",") ? qrCodeBase64.split(",")[1] : qrCodeBase64;
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                Image qrImage = new Image(new ByteArrayInputStream(imageBytes));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/QRCodeView.fxml"));
                VBox qrRoot = loader.load();
                QRCodeViewController qrController = loader.getController();
                qrController.setQrCodeImage(qrImage);

                Stage qrStage = new Stage();
                qrStage.setTitle("Mã QR của " + document.getTitle());
                qrStage.setScene(new Scene(qrRoot));
                qrStage.initOwner(shareButton.getScene().getWindow());
                qrStage.initModality(Modality.APPLICATION_MODAL);
                qrStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể hiển thị mã QR.");
            }
        }
    }

    // Đóng cửa sổ
    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }

    // Hiển thị bình luận và số sao
    private void displayRatingAndComments() {
        String sql = "SELECT count_star FROM books WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, document.getIsbn());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double averageRating = rs.getDouble("count_star");
                ratingLabel.setText("Số sao trung bình: " + averageRating + "/5");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Lấy các bình luận
        ObservableList<String> commentsList = FXCollections.observableArrayList();
        String commentSql = "SELECT comment FROM comments WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(commentSql)) {
            pstmt.setString(1, document.getIsbn());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                commentsList.add(rs.getString("comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Hiển thị bình luận vào UI
        VBox commentsVBox = new VBox();
        for (String comment : commentsList) {
            Label commentLabel = new Label(comment);
            commentsVBox.getChildren().add(commentLabel);
        }
        commentsContainer.getChildren().add(commentsVBox);
    }

    // Cập nhật đánh giá
    @FXML
    private void handleSubmitReview() {
        String commentText = commentArea.getText();
        if (commentText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Bình luận không được để trống.");
            return;
        }

        // Lưu bình luận
        Comment comment = new Comment(userId, document.getIsbn(), commentText);
        boolean success = commentDAO.addComment(comment);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cảm ơn bạn đã bình luận!");
            commentArea.clear();
            loadComments(); // Tải lại danh sách bình luận
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm bình luận. Vui lòng thử lại.");
        }
    }

    private void updateBookRating(String isbn) {
        double totalStars = 0;
        int totalReviews = 0;

        String sql = "SELECT rating FROM comments WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                totalStars += rs.getInt("rating");
                totalReviews++;
            }

            double averageStars = totalReviews > 0 ? totalStars / totalReviews : 0;

            // Cập nhật số sao vào bảng books
            String updateSql = "UPDATE books SET count_star = ? WHERE isbn = ?";
            try (PreparedStatement updatePstmt = connection.prepareStatement(updateSql)) {
                updatePstmt.setDouble(1, averageStars);
                updatePstmt.setString(2, isbn);
                updatePstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void initialize() {
        initRatingStars();
    }

    private void initRatingStars() {
        for (int i = 1; i <= 5; i++) {
            ImageView star = createStar(i);
            ratingStars.getChildren().add(star);
        }
    }

    private ImageView createStar(int starRating) {
        ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/image/star_empty.jpg")));
        star.setFitHeight(30);
        star.setFitWidth(30);
        star.setOnMouseClicked(event -> {
            updateRating(starRating);
        });
        return star;
    }

    private void updateRating(int starRating) {
        currentRating = starRating;
        ratingLabel.setText("Đánh giá: " + currentRating + "/5");
        for (int i = 0; i < ratingStars.getChildren().size(); i++) {
            ImageView star = (ImageView) ratingStars.getChildren().get(i);
            if (i < starRating) {
                star.setImage(new Image(getClass().getResourceAsStream("/image/star_filled.jpg")));
            } else {
                star.setImage(new Image(getClass().getResourceAsStream("/image/star_empty.jpg")));
            }
        }
    }
}
