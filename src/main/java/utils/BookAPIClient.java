package utils;

import Objects.Document;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class BookAPIClient {
    public static List<Document> fetchBooks(String query) throws IOException, InterruptedException {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes" + query;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

        List<Document> books = new ArrayList<>();
        if (jsonResponse.has("items")) {
            JsonArray items = jsonResponse.getAsJsonArray("items");
            for (var item : items) {
                JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                String isbn = volumeInfo.has("industryIdentifiers") ?
                        volumeInfo.getAsJsonArray("industryIdentifiers")
                                .get(0).getAsJsonObject().get("identifier").getAsString() : "N/A";
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "N/A";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").toString() : "N/A";
                String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : "N/A";
                String categories = volumeInfo.has("categories") ? volumeInfo.getAsJsonArray("categories").toString() : "N/A";
                String thumbnailLink = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail") ?
                        volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "N/A";
                String previewLink = volumeInfo.has("previewLink") ? volumeInfo.get("previewLink").getAsString() : "N/A";
                int quantity = volumeInfo.has("quantity") ? volumeInfo.get("quantity").getAsInt() : 0;


                books.add(new Document(isbn, title, authors, description, categories, thumbnailLink, previewLink, quantity));
            }
        }
        return books;
    }
}
