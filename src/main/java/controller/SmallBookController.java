package controller;

import Objects.Document;
import dao.BookDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.print.Book;
import java.net.URL;
import java.util.ResourceBundle;

public class SmallBookController {
    @FXML
    private ImageView bookImage;

    @FXML
    private Label titleLabel;

    @FXML
    private Label authorLabel;


    public void loadBookInfo(Document doc) {
        bookImage.setImage(new Image(doc.getThumbnailLink()));
        titleLabel.setText(doc.getTitle());
        authorLabel.setText(doc.getAuthors());

    }


}
