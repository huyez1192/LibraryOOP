package controller;

import Objects.Document;
import dao.BookDAO;
import dao.BorrowRecordDAO;
import dao.RequestDAO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    // Thành phần từ HomeController
    @FXML
    private GridPane bookContainer;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    // Thành phần từ LibraryHomeController
    @FXML
    private VBox suggestionBox; // Đảm bảo khai báo này

    @FXML
    private VBox suggestionContent; // Thêm khai báo này

    @FXML
    private VBox overlay;

    private List<Document> recommended;
    private int userId; // ID người dùng, cần lấy từ đăng nhập hoặc session
    private RequestDAO requestDAO = new RequestDAO(); // Khởi tạo RequestDAO
    private BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO();

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final OkHttpClient client = new OkHttpClient();

    // Phương thức để gán userId từ đăng nhập
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Phương thức mở chi tiết sách
    private void openBookDetail(Document document) {
        try {
            // Tạo FXMLLoader để load file FXML cho trang chi tiết sách
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/bookDetail.fxml"));
            BorderPane bookDetailPage = fxmlLoader.load();

            // Lấy controller của trang chi tiết sách và load thông tin sách
            BookDetailsController bookDetailsController = fxmlLoader.getController();
            bookDetailsController.loadBookDetails(document, userId, requestDAO, borrowRecordDAO);  // Truyền đủ ba tham số

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Khởi tạo các gợi ý tìm kiếm
        initializeSearchSuggestions();

        // Tải và hiển thị sách được khuyến nghị
        loadRecommendedBooks();
    }

    // Khởi tạo các gợi ý tìm kiếm
    private void initializeSearchSuggestions() {
        // Xử lý sự kiện khi người dùng nhập trong thanh tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                fetchBookSuggestions(newValue);
            } else {
                hideSuggestions();
            }
        });

        // Ẩn danh sách gợi ý khi mất tiêu điểm
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                hideSuggestions();
            }
        });

        // Xử lý sự kiện khi nhấn nút tìm kiếm
        searchButton.setOnAction(event -> onSearchButtonClick());
    }

    // Phương thức lấy gợi ý sách từ Google Books API
    private void fetchBookSuggestions(String query) {
        String url = API_URL + query;

        Request request = new Request.Builder().url(url).build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    updateSuggestionBox(jsonResponse);
                } else {
                    System.err.println("API Error: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Cập nhật hộp gợi ý với dữ liệu từ API
    private void updateSuggestionBox(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray items = jsonObject.getAsJsonArray("items");

        Platform.runLater(() -> {
            if (suggestionBox != null && suggestionContent != null) { // Thêm kiểm tra null
                suggestionContent.getChildren().clear();
                if (items != null) {
                    int maxSuggestions = Math.min(items.size(), 5);
                    for (int i = 0; i < maxSuggestions; i++) {
                        JsonObject book = items.get(i).getAsJsonObject();
                        JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
                        String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";

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

                        if (volumeInfo.has("imageLinks")) {
                            JsonObject imageLinks = volumeInfo.getAsJsonObject("imageLinks");
                            if (imageLinks.has("thumbnail")) {
                                String thumbnailUrl = imageLinks.get("thumbnail").getAsString();
                                // Thay đổi URL nếu cần thiết (https thay vì http)
                                thumbnailUrl = thumbnailUrl.replace("http://", "https://");
                                bookImageView.setImage(new Image(thumbnailUrl, true));
                            } else {
                                // Đặt ảnh mặc định nếu không có thumbnail
                                bookImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_book.png")));
                            }
                        } else {
                            // Đặt ảnh mặc định nếu không có imageLinks
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
                        suggestionBoxItem.setOnMouseClicked(event -> {
                            searchField.setText(title); // Điền tiêu đề vào thanh tìm kiếm
                            hideSuggestions();
                            // Có thể thực hiện hành động tìm kiếm hoặc mở chi tiết sách ngay tại đây
                            // Ví dụ:
                            // openBookDetailByTitle(title);
                        });

                        // Thêm HBox vào suggestionContent
                        suggestionContent.getChildren().add(suggestionBoxItem);
                    }
                    suggestionBox.setVisible(true); // Hiển thị danh sách gợi ý
                } else {
                    hideSuggestions();
                }
            } else {
                System.err.println("suggestionBox or suggestionContent is null");
            }
        });
    }

    // Xử lý khi nhấn nút tìm kiếm
    private void onSearchButtonClick() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            fetchBookSuggestions(query);
            // Có thể thực hiện hành động tìm kiếm như hiển thị kết quả tìm kiếm
        }
    }

    // Ẩn hộp gợi ý
    private void hideSuggestions() {
        Platform.runLater(() -> {
            if (suggestionBox != null) { // Thêm kiểm tra null
                suggestionBox.setVisible(false);  // Ẩn hộp gợi ý
                suggestionBox.setManaged(false); // Không chiếm không gian
            } else {
                System.err.println("suggestionBox is null in hideSuggestions");
            }
        });
    }

    // Hiển thị hộp gợi ý
    private void showSuggestions() {
        Platform.runLater(() -> {
            if (suggestionBox != null) { // Thêm kiểm tra null
                suggestionBox.setVisible(true);  // Hiển thị hộp gợi ý
                suggestionBox.setManaged(true); // Chiếm không gian
            } else {
                System.err.println("suggestionBox is null in showSuggestions");
            }
        });
    }

    // Tải và hiển thị sách được khuyến nghị từ cơ sở dữ liệu
    private void loadRecommendedBooks() {
        recommended = documents();
        System.out.println(recommended.toString());
        System.out.println(recommended.size());
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
                bookBox.setOnMouseClicked(event -> openBookDetail(document));

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
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Đổi Scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}