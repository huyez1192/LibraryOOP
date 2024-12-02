package controller;

import Objects.Document;
import dao.BookDAO;
import dao.RequestDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private GridPane bookContainer;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    private List<Document> recommended;
    private int userId ; // ID người dùng, cần phải lấy từ đăng nhập hoặc session
    private RequestDAO requestDAO = new RequestDAO();  // Khởi tạo RequestDAO

    // Phương thức để gán userId từ đăng nhập

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private void openBookDetail(Document document) {
        try {
            // Tạo FXMLLoader để load file FXML cho trang chi tiết sách
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/bookDetail.fxml"));
            BorderPane bookDetailPage = fxmlLoader.load();

            // Lấy controller của trang chi tiết sách và load thông tin sách
            BookDetailsController bookDetailsController = fxmlLoader.getController();
            bookDetailsController.loadBookDetails(document, userId, requestDAO);  // Truyền đủ ba tham số

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
        recommended = documents();
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

    private List<Document> documents() {
        List<Document> ls = new ArrayList<>();
        BookDAO bookDAO = new BookDAO();
        ls = bookDAO.getAllDocuments();
        return ls;
    }

    @FXML
    private void search(ActionEvent event) {
    }
}
