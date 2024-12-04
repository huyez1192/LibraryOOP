package utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import dao.BookDAO;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksAPI {
    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Thay bằng API Key của bạn
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public static void main(String[] args) {
        // Danh sách ISBN
        List<String> queries = List.of(
                "9781032919980", "9781439894057", "9783540403869", "9781614445166",
                "9783030451936", "9780883859155", "9781461261353", "9780521592710",
                "9781139435161", "9780849371646", "9781470474553", "9780128010501",
                "9781536191738", "9789811237157", "9781786343468", "9780821841488",
                "9780792369523", "9781461200079", "9789401018531", "9781931233675",
                "9780470317945", "9781134672462", "9780415977098", "9781351436700",
                "9780191604805", "9781101212943", "9781293565322", "9781498702683",
                "9780306836565", "9781846281686", "9783540761976", "9780817644970",
                "9781119062790", "9781511689250"
        );

        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
        for (String isbn : queries) {
            googleBooksAPI.getData(isbn);
        }
    }

    public void getData(String isbn) {
        OkHttpClient client = new OkHttpClient();

        // Sửa URL: thêm dấu hai chấm sau 'isbn'
        String url = BASE_URL + "?q=isbn:" + isbn + "&key=" + API_KEY;

        // Tạo Request
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            // Gửi Request và nhận Response
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                // Đọc nội dung phản hồi
                String jsonResponse = response.body().string();
                System.out.println("Results for ISBN: " + isbn);
                saveBooksToDatabase(jsonResponse);
            } else {
                System.out.println("Error for ISBN " + isbn + ": " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Error for ISBN " + isbn + ": " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Phương thức lấy danh sách sách dựa trên các từ khóa (tìm kiếm theo từ khóa)
    public static List<BookSuggestion> getSuggestionsByKeyword(List<String> queries) {
        List<BookSuggestion> suggestions = new ArrayList<>();

        for (String query : queries) {
            // URL encode từ khóa để tránh lỗi cú pháp
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BASE_URL + "?q=" + encodedQuery + "&key=" + API_KEY;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    System.out.println("API Response for query \"" + query + "\": " + jsonResponse);
                    suggestions.addAll(parseBooks(jsonResponse));
                } else {
                    System.out.println("API request failed for query \"" + query + "\": " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return suggestions;
    }

    // Phương thức lấy danh sách sách dựa trên ISBN
    public static List<BookSuggestion> getSuggestionsByISBN(List<String> isbns) {
        List<BookSuggestion> suggestions = new ArrayList<>();

        for (String isbn : isbns) {
            String url = BASE_URL + "?q=isbn:" + isbn + "&key=" + API_KEY;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    suggestions.addAll(parseBooks(jsonResponse));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return suggestions;
    }

    // Phương thức phân tích JSON và tạo danh sách BookSuggestion
    public static List<BookSuggestion> parseBooks(String jsonResponse) {
        List<BookSuggestion> suggestions = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        JsonArray items = jsonObject.getAsJsonArray("items");
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JsonObject book = items.get(i).getAsJsonObject();
                JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");

                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";
                String thumbnail = null;
                if (volumeInfo.has("imageLinks")) {
                    JsonObject imageLinks = volumeInfo.getAsJsonObject("imageLinks");
                    if (imageLinks.has("thumbnail")) {
                        thumbnail = imageLinks.get("thumbnail").getAsString();
                        // Google Books API thường trả về URL bắt đầu bằng "http", cần chuyển thành "https"
                        if (thumbnail.startsWith("http://")) {
                            thumbnail = "https://" + thumbnail.substring(7);
                        }
                    }
                }

                suggestions.add(new BookSuggestion(title, thumbnail));
            }
        }

        return suggestions;
    }

    // Phương thức lưu sách vào cơ sở dữ liệu
    public static void saveBooksToDatabase(String jsonResponse) throws SQLException {
        List<BookSuggestion> books = parseBooks(jsonResponse);

        String sql = "INSERT INTO books (title, thumbnail) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "gem07012005");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (BookSuggestion book : books) {
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getThumbnail());
                stmt.executeUpdate();
            }
        }
    }

    // Lớp BookSuggestion để lưu trữ thông tin sách
    public static class BookSuggestion {
        private final String title;
        private final String thumbnail;

        public BookSuggestion(String title, String thumbnail) {
            this.title = title;
            this.thumbnail = thumbnail;
        }

        public String getTitle() {
            return title;
        }

        public String getThumbnail() {
            return thumbnail;
        }
    }
}
