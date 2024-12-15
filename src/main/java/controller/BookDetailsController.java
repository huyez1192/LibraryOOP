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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import connect.MySQLConnection;
import Objects.Comment;
import utils.UserIdSingleton;

public class BookDetailsController {

    private CommentDAO commentDAO;
    private RequestDAO requestDAO;
    private int userId = UserIdSingleton.getInstance().getUserId();
    private Document document;
    private BorrowRecordDAO borrowRecordDAO;
    private Connection connection;

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
    private Label ratingLabel;

    @FXML
    private TextArea commentArea;

    @FXML
    private Button submitReviewButton;

    @FXML
    private ScrollPane commentsContainer;

    @FXML
    private VBox commentsVBox;

    @FXML
    private HBox ratingStars;

    private int currentRating = 0;

    public BookDetailsController() {
        this.connection = MySQLConnection.getConnection();
        this.commentDAO = new CommentDAO(connection);
    }

    public void loadBookDetails(Document document, int userId, RequestDAO requestDAO, BorrowRecordDAO borrowRecordDAO) {
        this.document = document;
        this.userId = userId;
        this.requestDAO = requestDAO;
        this.borrowRecordDAO = borrowRecordDAO;

        bookTitle.setText(document.getTitle());
        bookAuthors.setText("Tác giả: " + document.getAuthors());
        bookCategory.setText("Thể loại: " + document.getCategories());
        bookDescription.setText(document.getDescription());

        if (document.getThumbnailLink() != null && !document.getThumbnailLink().isEmpty()) {
            bookThumbnail.setImage(new Image(document.getThumbnailLink()));
        } else {
            bookThumbnail.setImage(new Image(getClass().getResourceAsStream("/images/default_book.png")));
        }

        double averageRating = calculateAverageRating(document.getIsbn());
        if (averageRating > 0) {
            ratingLabel.setText(String.format("Số sao trung bình: %.1f/5", averageRating));
        } else {
            ratingLabel.setText("Chưa có đánh giá");
        }

        loadComments();
        displayRatingStars();
        checkRequestStatus();
    }

    private double calculateAverageRating(String isbn) {
        return commentDAO.getAverageRating(isbn);
    }

    private void loadComments() {
        commentsVBox.getChildren().clear();
        List<Comment> comments = commentDAO.getCommentsByIsbn(document.getIsbn());

        for (Comment comment : comments) {
            VBox commentBox = new VBox();
            commentBox.setSpacing(5);
            commentBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 10;");

            Label userLabel = new Label("Người dùng: " + comment.getUserName());
            userLabel.setStyle("-fx-font-weight: bold;");

            Label contentLabel = new Label(comment.getCommentText());
            contentLabel.setWrapText(true);

            commentBox.getChildren().addAll(userLabel, contentLabel);
            commentsVBox.getChildren().add(commentBox);
        }
    }

    private void displayRatingStars() {
        ratingStars.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            ImageView star = createStar(i);
            ratingStars.getChildren().add(star);
        }
    }

    private ImageView createStar(int starRating) {
        ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/image/star_empty.jpg")));
        star.setFitHeight(30);
        star.setFitWidth(30);
        star.setOnMouseClicked(event -> updateRating(starRating));
        return star;
    }

    private void updateRating(int starRating) {
        currentRating = starRating;
        for (int i = 0; i < ratingStars.getChildren().size(); i++) {
            ImageView star = (ImageView) ratingStars.getChildren().get(i);
            if (i < starRating) {
                star.setImage(new Image(getClass().getResourceAsStream("/image/star_filled.jpg")));
            } else {
                star.setImage(new Image(getClass().getResourceAsStream("/image/star_empty.jpg")));
            }
        }
    }

    @FXML
    private void handleSubmitReview() {
        String commentText = commentArea.getText();
        if (commentText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Bình luận không được để trống.");
            return;
        }

        if (currentRating == 0) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Bạn chưa chọn số sao đánh giá.");
            return;
        }

        Comment comment = new Comment(userId, document.getIsbn(), commentText, currentRating);
        if (commentDAO.addComment(comment)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cảm ơn bạn đã bình luận!");
            commentArea.clear();
            updateRating(0);
            loadComments();
            ratingLabel.setText(String.format("Số sao trung bình: %.1f/5", calculateAverageRating(document.getIsbn())));
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm bình luận. Vui lòng thử lại.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void checkRequestStatus() {
        boolean borrowedExists = borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn());
        boolean requestExists = requestDAO.checkIfRequestExists(userId, document.getIsbn());

        if (borrowedExists) {
            borrowButton.setText("Trả sách");
            borrowButton.setOnAction(event -> handleReturnBook());
        } else if (requestExists) {
            borrowButton.setText("Chờ phê duyệt");
            borrowButton.setDisable(true);
        } else {
            borrowButton.setText("Mượn sách");
            borrowButton.setOnAction(event -> handleBorrowBook());
        }
    }

    @FXML
    private void handleBorrowBook() {
        if (!borrowRecordDAO.checkIfBorrowedExists(userId, document.getIsbn())
                && !requestDAO.checkIfRequestExists(userId, document.getIsbn())) {
            requestDAO.addRequest(userId, document.getIsbn(), new Date(System.currentTimeMillis()));
            showAlert(Alert.AlertType.INFORMATION, "Yêu cầu mượn sách", "Yêu cầu mượn sách đã được gửi.");
            checkRequestStatus();
        }
    }

    @FXML
    private void handleReturnBook() {
        if (borrowRecordDAO.deleteBorrowRecord(userId, document.getIsbn())) {
            showAlert(Alert.AlertType.INFORMATION, "Trả sách thành công", "Bạn đã trả sách thành công.");
            checkRequestStatus();
        }
    }

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


    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
}