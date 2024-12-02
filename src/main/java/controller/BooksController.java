package controller;

import Objects.Document;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BooksController {
    @FXML
    private TextField bookname;

    @FXML
    private TextField bookisbn;

    @FXML
    private TextField bookauthor;

    @FXML
    private TextField bookquantity;

    @FXML
    public void loadBookInfo(Document book) {
        bookname.setText(book.getTitle());
        bookisbn.setText(book.getIsbn());
        bookauthor.setText(book.getAuthors());
        bookquantity.setText(book.getQuantity());
    }
}
