package Main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class GoogleBooksAPI {
    private static final String API_KEY = "AIzaSyAc4FaGOKPtQ2fogm2oMwiVpF5Q1Tl4seI";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=java+programming";

    public static void main(String[] args) {
//        String query = "9781499462746"; // Từ khóa tìm kiếm
//        String url = BASE_URL + "?q=isbn:" + query + "&key=" + API_KEY;

//        // Tạo OkHttpClient
//        OkHttpClient client = new OkHttpClient();
//
//        // Tạo yêu cầu HTTP
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        // Gửi yêu cầu và xử lý phản hồi
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                String jsonData = response.body().string();
//                System.out.println("Response Data: " + jsonData);
//            } else {
//                System.out.println("Request failed: " + response.code());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        OkHttpClient client = new OkHttpClient();

        // Tạo Request
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try {
            // Gửi Request và nhận Response
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                // Đọc nội dung phản hồi
                String jsonResponse = response.body().string();
                parseBooks(jsonResponse);
            } else {
                System.out.println("Error: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseBooks(String jsonResponse) {
        // Sử dụng Gson để phân tích JSON
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        // Lấy danh sách sách từ "items"
        JsonArray items = jsonObject.getAsJsonArray("items");

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JsonObject book = items.get(i).getAsJsonObject();
                JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");

                // Lấy thông tin cơ bản
                String title = volumeInfo.get("title").getAsString();
                String authors = volumeInfo.has("authors") ? volumeInfo.get("authors").toString() : "Unknown";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";

                System.out.println("Title: " + title);
                System.out.println("Authors: " + authors);
                System.out.println("Publisher: " + publisher);
                System.out.println("Published Date: " + publishedDate);
                System.out.println("-------------");
            }
        } else {
            System.out.println("No books found.");
        }
    }
}
