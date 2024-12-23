package dao;

import Objects.Document;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import connect.MySQLConnection;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static connect.MySQLConnection.getConnection;

public class BookDAO {
    public static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=java+programming";

    private static final String INSERT_QUERY = "INSERT INTO Books (isbn, title, authors, description, categories, thumbnail_link, previewLink, publisher, quantity) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE title=VALUES(title), authors=VALUES(authors), description=VALUES(description), categories=VALUES(categories), "
            + "thumbnail_link=VALUES(thumbnail_link), previewLink=VALUES(previewLink), publisher=VALUES(publisher), quantity=VALUES(quantity)";

    private Connection connection;

    public BookDAO() {
        this.connection = MySQLConnection.getConnection(); // Giả sử bạn đã có phương thức này để kết nối đến CSDL
    }

    public static void saveBooksToDatabase(String jsonResponse) throws SQLException {

        String insertQuery = "INSERT INTO Books (isbn, title, authors, description, categories, thumbnail_link, previewLink, quantity) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
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
                int quantity = volumeInfo.has("quantity") ? volumeInfo.get("quantity").getAsInt() : 0;

                // Gán giá trị vào PreparedStatement
                preparedStatement.setString(1, isbn);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, authors);
                preparedStatement.setString(4, description);
                preparedStatement.setString(5, categories);
                preparedStatement.setString(6, thumbnail_link);
                preparedStatement.setString(7, previewLink);
                preparedStatement.setInt(8, quantity);

                // Thực thi lệnh INSERT
                preparedStatement.executeUpdate();
                System.out.println("Books saved to the database.");
            }
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

    public List<Document> getByTitle(String title) {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE title LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Document doc = new Document(
                            rs.getString("isbn"),
                            rs.getString("title"),
                            rs.getString("authors"),
                            rs.getString("description"),
                            rs.getString("categories"),
                            rs.getString("thumbnail_link"),
                            rs.getString("previewLink"),
                            rs.getInt("quantity")
                    );
                    documents.add(doc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    public List<Document> getByIsbn(String isbn) {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Document doc = new Document(
                            rs.getString("isbn"),
                            rs.getString("title"),
                            rs.getString("authors"),
                            rs.getString("description"),
                            rs.getString("categories"),
                            rs.getString("thumbnail_link"),
                            rs.getString("previewlink"),
                            rs.getInt("quantity")
                    );
                    documents.add(doc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    public void delete(String isbn) {
        String query = "DELETE FROM Books WHERE isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, isbn);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String isbn, int quantity) {
        String query = "UPDATE Books SET quantity = ? WHERE isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, quantity);
            ps.setString(2, isbn);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Document> getAllDocuments() {
//        List<Objects.Document> docs=new ArrayList<>();
//        return docs;
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM Books";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Document doc = new Document(
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("authors"),
                        rs.getString("description"),
                        rs.getString("categories"),
                        rs.getString("thumbnail_link"),
                        rs.getString("previewLink"),
                        rs.getInt("quantity")
                );
                documents.add(doc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    public List<Document> getByCategories(String categories) {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE categories = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, categories);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Document doc = new Document(
                            rs.getString("isbn"),
                            rs.getString("title"),
                            rs.getString("authors"),
                            rs.getString("description"),
                            rs.getString("categories"),
                            rs.getString("thumbnail_link"),
                            rs.getString("previewlink"),
                            rs.getInt("quantity")
                    );
                    documents.add(doc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

}


