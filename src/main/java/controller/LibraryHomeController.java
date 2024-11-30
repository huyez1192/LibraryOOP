package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class LibraryHomeController {
    @FXML
    private VBox overlay;

//    @FXML
//    private void showOverlay() {
//        overlay.setVisible(true);
//    }
//
//    @FXML
//    private void hideOverlay() {
//        overlay.setVisible(false);

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestionBox;

    @FXML
    private Button searchButton;


    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final OkHttpClient client = new OkHttpClient();


    @FXML
    public void initialize() {
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
    }

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


    private void updateSuggestionBox(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray items = jsonObject.getAsJsonArray("items");

        Platform.runLater(() -> {
            suggestionBox.getChildren().clear();
            if (items != null) {
                int maxSuggestions = Math.min(items.size(), 5);
                for (int i = 0; i < maxSuggestions; i++) {
                    JsonObject book = items.get(i).getAsJsonObject();
                    JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";

                    // Tạo Label cho từng gợi ý
                    Label suggestion = new Label(title);
                    suggestion.setStyle("-fx-padding: 8; -fx-font-size: 14px; -fx-cursor: hand;");
                    suggestion.setOnMouseClicked(event -> {
                        searchField.setText(title); // Điền tiêu đề vào thanh tìm kiếm
                        hideSuggestions();
                    });
                    suggestionBox.getChildren().add(suggestion);
                }
                suggestionBox.setVisible(true); // Hiển thị danh sách gợi ý
            } else {
                hideSuggestions();
            }
        });
    }

    // Phương thức xử lý khi nhấn nút tìm kiếm
    @FXML
    private void onSearchButtonClick() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            fetchBookSuggestions(query);
        }
    }

    private void hideSuggestions() {
        Platform.runLater(() -> {
            suggestionBox.setVisible(false);  // Ẩn hộp gợi ý
            suggestionBox.setManaged(false); // Không chiếm không gian
        });
    }
    private void showSuggestions() {
        Platform.runLater(() -> {
            suggestionBox.setVisible(true);  // Hiển thị hộp gợi ý
            suggestionBox.setManaged(true); // Chiếm không gian
        });
    }

}
