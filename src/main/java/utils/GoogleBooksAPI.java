package utils;//package Main;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import java.io.IOException;
//
//public class GoogleBooksAPI {
//    private static final String API_KEY = "AIzaSyAc4FaGOKPtQ2fogm2oMwiVpF5Q1Tl4seI";
//    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
//
//    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=java+programming";
//
//    public static void main(String[] args) {
//        String isbn = "9781499462746"; // Từ khóa tìm kiếm
//        String url = BASE_URL + "?q=isbn:" + isbn + "&key=" + API_KEY;
//
////        // Tạo OkHttpClient
////        OkHttpClient client = new OkHttpClient();
////
////        // Tạo yêu cầu HTTP
////        Request request = new Request.Builder()
////                .url(url)
////                .build();
////
////        // Gửi yêu cầu và xử lý phản hồi
////        try (Response response = client.newCall(request).execute()) {
////            if (response.isSuccessful()) {
////                String jsonData = response.body().string();
////                System.out.println("Response Data: " + jsonData);
////            } else {
////                System.out.println("Request failed: " + response.code());
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        OkHttpClient client = new OkHttpClient();
//
//        // Tạo Request
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        try {
//            // Gửi Request và nhận Response
//            Response response = client.newCall(request).execute();
//
//            if (response.isSuccessful() && response.body() != null) {
//                // Đọc nội dung phản hồi
//                String jsonResponse = response.body().string();
//                parseBooks(jsonResponse);
//            } else {
//                System.out.println("Error: " + response.code());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void parseBooks(String jsonResponse) {
//        // Sử dụng Gson để phân tích JSON
//        Gson gson = new Gson();
//        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
//
//        // Lấy danh sách sách từ "items"
//        JsonArray items = jsonObject.getAsJsonArray("items");
//
//        if (items != null) {
//            for (int i = 0; i < items.size(); i++) {
//                JsonObject book = items.get(i).getAsJsonObject();
//                JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
//
//                // Lấy thông tin cơ bản
//                String title = volumeInfo.get("title").getAsString();
//                String authors = volumeInfo.has("authors") ? volumeInfo.get("authors").toString() : "Unknown";
//                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
//                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
//
//                System.out.println("Title: " + title);
//                System.out.println("Authors: " + authors);
//                System.out.println("Publisher: " + publisher);
//                System.out.println("Published Date: " + publishedDate);
//                System.out.println("-------------");
//            }
//        } else {
//            System.out.println("No books found.");
//        }
//    }
//}



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import static dao.BookDAO.saveBooksToDatabase;

public class GoogleBooksAPI {
    private static final String API_KEY = "AIzaSyAc4FaGOKPtQ2fogm2oMwiVpF5Q1Tl4seI";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public static void main(String[] args) {
        // Danh sách ISBN
        List<String> queries = Arrays.asList(
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

        GoogleBooksAPI googleBooksAPI=new GoogleBooksAPI();
        for (int i=0; i<queries.size();i++) {
            googleBooksAPI.getData(queries.get(i));
        }
    }
    
    public void getData(String isbn) {
        OkHttpClient client = new OkHttpClient();
        
        String url = BASE_URL + "?q=isbn" + isbn + "&key=" + API_KEY;
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

//    private static void parseBooks(String jsonResponse) {
//        // Sử dụng Gson để phân tích JSON
//        Gson gson = new Gson();
//        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
//
//        // Lấy danh sách sách từ "items"
//        JsonArray items = jsonObject.getAsJsonArray("items");
//
//        if (items != null) {
//            for (int i = 0; i < items.size(); i++) {
//                JsonObject book = items.get(i).getAsJsonObject();
//                JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
//                String isbn = volumeInfo.has("industryIdentifiers") ? getIsbn(volumeInfo, "ISBN_13") : null;
//                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : null;
//                String authors = volumeInfo.has("authors") ? volumeInfo.get("authors").toString() : null;
//                String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : null;
//                String categories = volumeInfo.has("categories") ? volumeInfo.get("categories").toString() : null;
//                String thumbnail_link = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
//                        ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
//                        : null;
//                String previewLink = volumeInfo.has("previewLink") ? volumeInfo.get("previewLink").getAsString() : null;
//                Book book = new Book(isbn, title, authors, publisher, publishedDate);
//                System.out.println("Title: " + title);
//                System.out.println("Authors: " + authors);
//                System.out.println("Publisher: " + publisher);
//                System.out.println("Published Date: " + publishedDate);
//                System.out.println("-------------");
//            }
//        } else {
//            System.out.println("No books found.");
//        }
//    }
}


//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.Arrays;
//import java.util.List;
//
//public class GoogleBooksAPI {
//    private static final String API_KEY = "AIzaSyAc4FaGOKPtQ2fogm2oMwiVpF5Q1Tl4seI";
//    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
//
//    public static void main(String[] args) {
//        // Danh sách ISBN
//        List<String> queries = Arrays.asList(
//                "9781032919980", "9781439894057", "9783540403869", "9781614445166",
//                "9783030451936", "9780883859155", "9781461261353", "9780521592710",
//                "9781139435161", "9780849371646", "9781470474553", "9780128010501",
//                "9781536191738", "9789811237157", "9781786343468", "9780821841488",
//                "9780792369523", "9781461200079", "9789401018531", "9781931233675",
//                "9780470317945", "9781134672462", "9780415977098", "9781351436700",
//                "9780191604805", "9781101212943", "9781293565322", "9781498702683",
//                "9780306836565", "9781846281686", "9783540761976", "9780817644970",
//                "9781119062790", "9781511689250"
//        );
//
//        OkHttpClient client = new OkHttpClient();
//
//        for (String isbn : queries) {
//            String url = BASE_URL + "?q=isbn:" + isbn + "&key=" + API_KEY;
//
//            // Tạo Request
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//
//            try {
//                // Gửi Request và nhận Response
//                Response response = client.newCall(request).execute();
//
//                if (response.isSuccessful() && response.body() != null) {
//                    // Đọc nội dung phản hồi
//                    String jsonResponse = response.body().string();
//                    System.out.println("Results for ISBN: " + isbn);
//                    parseBooks(jsonResponse);
//                } else {
//                    System.out.println("Error for ISBN " + isbn + ": " + response.code());
//                }
//            } catch (IOException e) {
//                System.out.println("Error for ISBN " + isbn + ": " + e.getMessage());
//            }
//        }
//    }
//
//    private static void parseBooks(String jsonResponse) {
//        // Sử dụng Gson để phân tích JSON
//        Gson gson = new Gson();
//        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
//
//        // Lấy danh sách sách từ "items"
//        JsonArray items = jsonObject.getAsJsonArray("items");
//
//        if (items != null) {
//            for (int i = 0; i < items.size(); i++) {
//                JsonObject book = items.get(i).getAsJsonObject();
//                JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
//
//                // Lấy thông tin cơ bản
//                String title = volumeInfo.get("title").getAsString();
//                String authors = volumeInfo.has("authors") ? volumeInfo.get("authors").toString() : "Unknown";
//                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
//                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
//
//                System.out.println("Title: " + title);
//                System.out.println("Authors: " + authors);
//                System.out.println("Publisher: " + publisher);
//                System.out.println("Published Date: " + publishedDate);
//                System.out.println("-------------");
//
//                // Gọi hàm lưu vào database
//                try {
//                    saveBookToDatabase(title, authors, publisher, publishedDate);
//                    System.out.println("Đã thêm sách thành công");
//                } catch (SQLException e) {
//                    System.out.println("Lỗi khi thêm sách vào database: " + e.getMessage());
//                }
//            }
//        } else {
//            System.out.println("No books found.");
//        }
//    }
//
//    private static void saveBookToDatabase(String title, String authors, String publisher, String publishedDate) throws SQLException {
//        // Cấu hình thông tin kết nối database
//        String url = "jdbc:mysql://localhost:3306/libraryy";
//        String user = "root";
//        String password = "huyen16125";
//
//        // Câu lệnh SQL
//        String sql = "INSERT INTO books (title, authors, publisher, published_date) VALUES (?, ?, ?, ?)";
//
//        // Kết nối và thực hiện câu lệnh
//        try (Connection conn = DriverManager.getConnection(url, user, password);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, title);
//            stmt.setString(2, authors);
//            stmt.setString(3, publisher);
//            stmt.setString(4, publishedDate);
//
//            stmt.executeUpdate();
//        }
//    }
//}


