package dao;

import Objects.Book;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import connect.MySQLConnection;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static connect.MySQLConnection.getConnection;

public class BookDAO {
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=java+programming";

    private static final String URL = "jdbc:mysql://localhost:3306/libraryy";
    private static final String USER = "root";
    private static final String PASSWORD = "huyen16125";

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Tạo Request
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                try {
                    saveBooksToDatabase(jsonResponse);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        } else {
            System.out.println("Error: " + response.code());
        }

    }

    public static void saveBooksToDatabase(String jsonResponse) throws SQLException {

        String insertQuery = "INSERT INTO Books (isbn, title, authors, description, categories, thumbnail_link, previewLink) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE title=VALUES(title), authors=VALUES(authors)";

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        JsonArray items = jsonObject.getAsJsonArray("items");

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JsonObject book = items.get(i).getAsJsonObject();
                JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");

                String isbn = volumeInfo.has("industryIdentifiers") ? getIsbn(volumeInfo, "ISBN_13") : null;
                if (isbn == null) {
                    System.out.println("Skipping book with missing ISBN.");
                    continue; // Bỏ qua bản ghi nếu không có ISBN
                }

                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : null;
                String authors = volumeInfo.has("authors") ? volumeInfo.get("authors").toString() : "Unknown";
                String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : "This book doesn't have any description";
                String categories = volumeInfo.has("categories") ? volumeInfo.get("categories").toString() : "Unknown";
                String thumbnail_link = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                        ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                        : null;
                String previewLink = volumeInfo.has("previewLink") ? volumeInfo.get("previewLink").getAsString() : "This book doesn't have any preview link";

                // Gán giá trị vào PreparedStatement
                preparedStatement.setString(1, isbn);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, authors);
                preparedStatement.setString(4, description);
                preparedStatement.setString(5, categories);
                preparedStatement.setString(6, thumbnail_link);
                preparedStatement.setString(7, previewLink);

                // Thực thi lệnh INSERT
                preparedStatement.executeUpdate();
                System.out.println("Books saved to the database.");
            }
            System.out.println("No books found.");
        }
    }

    private static String getIsbn(JsonObject volumeInfo, String type) {
        JsonArray industryIdentifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
        for (int j = 0; j < industryIdentifiers.size(); j++) {
            JsonObject identifier = industryIdentifiers.get(j).getAsJsonObject();
            if (identifier.get("type").getAsString().equals(type)) {
                return identifier.get("identifier").getAsString();
            }
        }
        return null;
    }
}


