package controller;

import Objects.Document;
import dao.BookDAO;
import dao.BorrowRecordDAO;
import dao.RequestDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    // Thành phần từ HomeController
    @FXML
    private GridPane bookContainer;

    @FXML
    private GridPane bookContainer1;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    // Thành phần từ LibraryHomeController
    @FXML
    private VBox suggestionBox; // Đảm bảo khai báo này

    @FXML
    private Button profileButton;

    @FXML
    private VBox suggestionContent; // Thêm khai báo này

    private List<Document> recommended;
    private List<Document> availableBooks; // Danh sách sách có sẵn cho gợi ý
    private int userId; // ID người dùng, cần lấy từ đăng nhập hoặc session
    private RequestDAO requestDAO = new RequestDAO();  // Khởi tạo RequestDAO
    private BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO(); // Khởi tạo BorrowRecordDAO

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Khởi tạo các gợi ý tìm kiếm
        initializeSearchSuggestions();

        // Tải và hiển thị sách được khuyến nghị
        loadRecommendedBooks();

        // Tải danh sách sách có sẵn cho gợi ý
        loadAvailableBooks();
    }

    // Khởi tạo các gợi ý tìm kiếm
    private void initializeSearchSuggestions() {
        // Xử lý sự kiện khi người dùng nhập trong thanh tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Search field changed: " + newValue);
            if (!newValue.trim().isEmpty()) {
                showSuggestions(newValue.trim());
            } else {
                hideSuggestions();
            }
        });

        // Ẩn danh sách gợi ý khi mất tiêu điểm
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                System.out.println("Search field lost focus");
                hideSuggestions();
            }
        });

        // Xử lý sự kiện khi nhấn nút tìm kiếm
        searchButton.setOnAction(event -> {
            System.out.println("Search button clicked");
            onSearchButtonClick();
        });
    }

    // Tải danh sách sách có sẵn từ cơ sở dữ liệu
    private void loadAvailableBooks() {
        availableBooks = new ArrayList<>();
        BookDAO bookDAO = new BookDAO();
        availableBooks = bookDAO.getAllDocuments();
        System.out.println("Loaded available books: " + availableBooks.size());
    }

    // Hiển thị các gợi ý dựa trên từ khóa nhập
    private void showSuggestions(String query) {
        System.out.println("Showing suggestions for query: " + query);
        // Tìm kiếm các sách có chứa từ khóa trong tiêu đề (không phân biệt chữ hoa chữ thường)
        List<Document> filteredBooks = availableBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()))
                .limit(5) // Giới hạn số lượng gợi ý
                .toList();

        if (filteredBooks.isEmpty()) {
            System.out.println("No suggestions found for query: " + query);
            hideSuggestions();
            return;
        }

        Platform.runLater(() -> {
            if (suggestionBox != null && suggestionContent != null) { // Thêm kiểm tra null
                suggestionContent.getChildren().clear();
                for (Document book : filteredBooks) {
                    String title = book.getTitle();
                    System.out.println("Adding suggestion: " + title);

                    // Tạo HBox cho từng gợi ý
                    HBox suggestionBoxItem = new HBox(10); // Khoảng cách giữa hình và tên sách
                    suggestionBoxItem.setPadding(new Insets(5));
                    suggestionBoxItem.setStyle("-fx-alignment: CENTER_LEFT; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5;");
                    suggestionBoxItem.setPrefHeight(40);

                    // Tạo ImageView cho hình nhỏ
                    ImageView bookImageView = new ImageView();
                    bookImageView.setFitHeight(30);
                    bookImageView.setFitWidth(30);
                    bookImageView.setPreserveRatio(true);
                    bookImageView.setSmooth(true);
                    bookImageView.setCache(true);

                    String thumbnailUrl = book.getThumbnailLink();
                    if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                        // Thay đổi URL nếu cần thiết (https thay vì http)
                        thumbnailUrl = thumbnailUrl.replace("http://", "https://");
                        bookImageView.setImage(new Image(thumbnailUrl, true));
                    } else {
                        // Đặt ảnh mặc định nếu không có URL ảnh
                        bookImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_book.png")));
                    }

                    // Tạo Label cho tên sách
                    Label bookTitleLabel = new Label(title);
                    bookTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                    bookTitleLabel.setWrapText(true);
                    bookTitleLabel.setMaxWidth(450); // Đảm bảo không vượt quá chiều rộng

                    // Thêm ImageView và Label vào HBox
                    suggestionBoxItem.getChildren().addAll(bookImageView, bookTitleLabel);

                    // Thêm sự kiện nhấn vào HBox
                    suggestionBoxItem.setOnMousePressed(event -> { // Sử dụng MousePressed
                        System.out.println("Suggestion item pressed: " + title);
                        if (searchField != null) {
                            searchField.setText(title); // Điền tiêu đề vào thanh tìm kiếm
                            hideSuggestions();
                            // Mở chi tiết sách ngay tại đây
                            openBookDetail(book);
                        } else {
                            System.err.println("searchField is null!");
                        }
                        event.consume(); // Ngăn chặn các sự kiện khác
                    });

                    // Thêm sự kiện nhấn trực tiếp vào Label (nếu cần)
                    bookTitleLabel.setOnMouseClicked(event -> {
                        System.out.println("Label clicked: " + title);
                        if (searchField != null) {
                            searchField.setText(title);
                            hideSuggestions();
                            openBookDetail(book);
                        } else {
                            System.err.println("searchField is null!");
                        }
                        event.consume(); // Ngăn chặn các sự kiện khác
                    });

                    // Thêm HBox vào suggestionContent
                    suggestionContent.getChildren().add(suggestionBoxItem);
                }
                suggestionBox.setVisible(true); // Hiển thị danh sách gợi ý
                suggestionBox.setManaged(true); // Chiếm không gian khi hiển thị
            } else {
                System.err.println("suggestionBox or suggestionContent is null");
            }
        });
    }

    // Xử lý khi nhấn nút tìm kiếm
    private void onSearchButtonClick() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            System.out.println("Performing search for: " + query);
            showSuggestions(query);
            // Có thể thực hiện hành động tìm kiếm như hiển thị kết quả tìm kiếm
            // Ví dụ: hiển thị tất cả các sách chứa từ khóa
        }
    }

    // Ẩn hộp gợi ý
    private void hideSuggestions() {
        System.out.println("Hiding suggestions");
        Platform.runLater(() -> {
            if (suggestionBox != null) { // Thêm kiểm tra null
                suggestionBox.setVisible(false);  // Ẩn hộp gợi ý
                suggestionBox.setManaged(false); // Không chiếm không gian
            } else {
                System.err.println("suggestionBox is null in hideSuggestions");
            }
        });
    }

    private void loadSuggestBooks() {
        UserDAO userDAO = new UserDAO(); // Tạo đối tượng UserDAO
        String favoriteCategory = userDAO.getUserFavoriteCategory(userId); // Gọi phương thức

        System.out.println(favoriteCategory);
        // Truy vấn các sách theo thể loại yêu thích
        BookDAO bookDAO = new BookDAO();
        List<Document> suggestedBooks = bookDAO.getByCategories(favoriteCategory);
        System.out.println(suggestedBooks);
        System.out.println("Suggested books loaded: " + suggestedBooks.size());

        int column = 0;
        int row = 1;

        try {
            for (Document document : suggestedBooks) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/small_book.fxml"));
                VBox bookBox = fxmlLoader.load();
                bookBox.getStylesheets().add(getClass().getResource("/css/cardStyling.css").toExternalForm());

                SmallBookController smallBookController = fxmlLoader.getController();
                smallBookController.loadBookInfo(document);

                // Gắn sự kiện nhấn vào thẻ sách để mở trang chi tiết
                bookBox.setOnMouseClicked(event -> {
                    System.out.println("Book box clicked: " + document.getTitle());
                    openBookDetail(document);
                });

                // Sắp xếp sách vào grid
                if (column == 8) {  // Giới hạn số cột, có thể thay đổi tùy ý
                    column = 0;
                    ++row;
                }

                // Thêm sách vào GridPane
                bookContainer1.add(bookBox, column++, row);
                GridPane.setMargin(bookBox, new Insets(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(bookContainer1); // In ra để kiểm tra xem bookContainer1 có null không
    }

    // Tải và hiển thị sách được khuyến nghị từ cơ sở dữ liệu
    private void loadRecommendedBooks() {
        recommended = documents();
        System.out.println("Recommended books loaded: " + recommended.size());
        int column = 0;
        int row = 1;


        try {
            for (Document document : recommended) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/small_book.fxml"));
                VBox bookBox = fxmlLoader.load();
                bookBox.getStylesheets().add(getClass().getResource("/css/cardStyling.css").toExternalForm());

                SmallBookController smallBookController = fxmlLoader.getController();
                smallBookController.loadBookInfo(document);

                // Gắn sự kiện nhấn vào thẻ sách để mở trang chi tiết
                bookBox.setOnMouseClicked(event -> {
                    System.out.println("Book box clicked: " + document.getTitle());
                    openBookDetail(document);
                });

                if (column == 8) {
                    column = 0;
                    ++row;
                }

                bookContainer.add(bookBox, column++, row);
                GridPane.setMargin(bookBox, new Insets(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách sách từ cơ sở dữ liệu
    private List<Document> documents() {
        List<Document> ls = new ArrayList<>();
        BookDAO bookDAO = new BookDAO();
        ls = bookDAO.getAllDocuments();
        return ls;
    }

    @FXML
    private void search(ActionEvent event) {
        // Có thể thêm logic tìm kiếm tại đây nếu cần
    }

    // Phương thức chuyển sang giao diện sách đã mượn
    @FXML
    public void switchToBorrowed(ActionEvent event) {
        try {

            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowedDocuments.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToMore(ActionEvent event) {
        try {
            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/More.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Phương thức chuyển sang giao diện Profile
    @FXML
    public void switchToProfile(ActionEvent event) {
        try {
            // Tải FXML của Profile
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();  // Đảm bảo đã tải FXML

            // Lấy controller từ FXML đã tải
            ProfileController profileController = loader.getController();

            // Kiểm tra xem profileController có được khởi tạo hay không
            if (profileController != null) {
                System.out.println("ProfileController đã được khởi tạo.");
                profileController.setUserId(userId);  // Gọi setUserId sau khi kiểm tra
            } else {
                System.err.println("Lỗi: ProfileController không được khởi tạo.");
            }

            // Mở cửa sổ Profile
            Stage stage = new Stage();
            stage.setTitle("Profile");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Ngăn tương tác với cửa sổ chính
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Không thể mở cửa sổ Profile.", Alert.AlertType.ERROR);
        }
    }




    // Phương thức mở chi tiết sách
    private void openBookDetail(Document document) {
        try {
            // Tạo FXMLLoader để load file FXML cho trang chi tiết sách
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/bookDetail.fxml"));
            Parent bookDetailPage = fxmlLoader.load();

            // Lấy controller của trang chi tiết sách và load thông tin sách
            BookDetailsController bookDetailsController = fxmlLoader.getController();
            bookDetailsController.loadBookDetails(document, userId, requestDAO, borrowRecordDAO);  // Truyền đủ bốn tham số

            // Mở cửa sổ mới để hiển thị chi tiết sách
            Stage stage = new Stage();
            Scene scene = new Scene(bookDetailPage);
            stage.setScene(scene);
            stage.setTitle(document.getTitle());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hiển thị thông báo
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> { // Đảm bảo chạy trên UI thread
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
