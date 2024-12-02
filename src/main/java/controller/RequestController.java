package controller;

import Objects.Request;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.swing.text.html.ImageView;

public class RequestController {
    @FXML
    private TextField requestid;

    @FXML
    private TextField userid;

    @FXML
    private TextField isbn;

    @FXML
    private TextField requestdate;

    @FXML
    private ImageView delete;

    @FXML
    public void loadRequestsInfo(Request request) {
        requestid.setText(String.valueOf(request.getRequestId()));
        userid.setText(String.valueOf(request.getUserId()));
        isbn.setText(String.valueOf(request.getIsbn()));
        requestdate.setText(String.valueOf(request.getRequestDate()));
    }
}
