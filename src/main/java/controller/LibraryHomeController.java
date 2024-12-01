package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import utils.GoogleBooksAPI;

import java.util.List;

public class LibraryHomeController {

    @FXML
    private TextField searchField;

    private Popup suggestionsPopup;

    @FXML
    public void search() {
        String keyword = searchField.getText(); // Lấy dữ liệu từ TextField
        System.out.println("Searching for: " + keyword);
        // Thêm logic xử lý tìm kiếm ở đây (ví dụ: gửi yêu cầu đến Google Books API)
    }

    @FXML
    public void initialize() {
        suggestionsPopup = new Popup();
        suggestionsPopup.setAutoHide(true);

        // Bắt sự kiện khi nhập liệu
        searchField.setOnKeyReleased(this::handleSearchKeyReleased);
    }

    private void handleSearchKeyReleased(KeyEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            suggestionsPopup.hide();
            return;
        }

        List<GoogleBooksAPI.BookSuggestion> suggestions = GoogleBooksAPI.getSuggestions(query);

        if (suggestions.isEmpty()) {
            suggestionsPopup.hide();
            return;
        }

        VBox suggestionBox = new VBox(5);
        suggestionBox.setStyle("-fx-background-color: #f0f0f3; -fx-border-color: #0078d4; -fx-border-width: 0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        suggestionBox.setPrefWidth(searchField.getWidth()); // Đặt chiều rộng trùng với thanh tìm kiếm

        for (GoogleBooksAPI.BookSuggestion suggestion : suggestions) {
            HBox item = createSuggestionItem(suggestion);
            suggestionBox.getChildren().add(item);
        }

        suggestionsPopup.getContent().clear();
        suggestionsPopup.getContent().add(suggestionBox);

        // Hiển thị popup ngay dưới thanh tìm kiếm
        suggestionsPopup.show(searchField,
                searchField.localToScreen(searchField.getBoundsInLocal()).getMinX(),
                searchField.localToScreen(searchField.getBoundsInLocal()).getMaxY() + 10);
    }


    private HBox createSuggestionItem(GoogleBooksAPI.BookSuggestion suggestion) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-padding: 5; -fx-cursor: hand; -fx-alignment: center-left;");

        // Thêm hình ảnh thu nhỏ
        ImageView imageView = new ImageView();
        if (suggestion.getThumbnail() != null) {
            imageView.setImage(new Image(suggestion.getThumbnail(), 30, 30, true, true));
        } else {
            imageView.setImage(new Image("file:src/main/resources/images/placeholder.png", 30, 30, true, true));
        }
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        // Thêm tên sách
        Text bookTitle = new Text(suggestion.getTitle());
        bookTitle.setStyle("-fx-font-size: 12px;");

        hbox.getChildren().addAll(imageView, bookTitle);

        // Xử lý sự kiện khi chọn gợi ý
        hbox.setOnMouseClicked(e -> {
            searchField.setText(suggestion.getTitle());
            suggestionsPopup.hide();
        });

        return hbox;
    }
}
