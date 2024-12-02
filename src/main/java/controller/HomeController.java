package controller;

import Objects.Document;
import dao.BookDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recommended = documents();
        System.out.println(recommended.toString());
        System.out.println(recommended.size());
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

                if (column == 8) {
                    column = 0;
                    ++ row;
                }

                bookContainer.add(bookBox, column++, row);
                GridPane.setMargin(bookBox, new Insets(10));
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
