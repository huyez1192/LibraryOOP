package utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.BookDAO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GoogleBooksAPI {
    private static final String API_KEY = "AIzaSyC-bqA6TTM4H5z_Hd-RcBT8lkL_ojwUtf0"; // Thay bằng API Key của bạn
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
                // Sử dụng BookDAO để lưu dữ liệu vào cơ sở dữ liệu
                BookDAO.saveBooksToDatabase(jsonResponse);
            } else {
                System.out.println("Error for ISBN " + isbn + ": " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Error for ISBN " + isbn + ": " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức phân tích JSON và tạo danh sách BookSuggestion
    public static List<BookSuggestion> parseBooks(String jsonResponse) {
        List<BookSuggestion> suggestions = new java.util.ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        JsonArray items = jsonObject.getAsJsonArray("items");
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JsonObject book = items.get(i).getAsJsonObject();
                JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");

                String isbn = getIsbn(volumeInfo, "ISBN_13"); // Hoặc loại ISBN phù hợp
                if (isbn == null) {
                    System.out.println("Skipping book with missing ISBN.");
                    continue; // Bỏ qua sách không có ISBN
                }

                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";
                String authors = volumeInfo.has("authors") ? String.join(", ", gson.fromJson(volumeInfo.get("authors"), String[].class)) : "Unknown Authors";
                String thumbnail = null;
                if (volumeInfo.has("imageLinks")) {
                    JsonObject imageLinks = volumeInfo.getAsJsonObject("imageLinks");
                    if (imageLinks.has("thumbnail")) {
                        thumbnail = imageLinks.get("thumbnail").getAsString();
                        if (thumbnail.startsWith("http://")) {
                            thumbnail = "https://" + thumbnail.substring(7);
                        }
                    }
                }

                suggestions.add(new BookSuggestion(isbn, title, authors, thumbnail));
            }
        }

        return suggestions;
    }

    private static String getIsbn(JsonObject volumeInfo, String type) {
        if (!volumeInfo.has("industryIdentifiers")) return null;
        JsonArray industryIdentifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
        for (int j = 0; j < industryIdentifiers.size(); j++) {
            JsonObject identifier = industryIdentifiers.get(j).getAsJsonObject();
            if (identifier.get("type").getAsString().equals(type)) {
                return identifier.get("identifier").getAsString();
            }
        }
        return null;
    }

    // Lớp BookSuggestion để lưu trữ thông tin sách
    public static class BookSuggestion {
        private final String isbn;
        private final String title;
        private final String authors;
        private final String thumbnail;

        public BookSuggestion(String isbn, String title, String authors, String thumbnail) {
            this.isbn = isbn;
            this.title = title;
            this.authors = authors;
            this.thumbnail = thumbnail;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthors() {
            return authors;
        }

        public String getThumbnail() {
            return thumbnail;
        }
    }
}
