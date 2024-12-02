package controller;

import Objects.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;


public class RequestsScreenController {
    @FXML
    private Button homeButton;


    @FXML
    private void handleHomeButton() {
        try {
            Stage stage = (Stage) homeButton.getScene().getWindow();

            Parent homeRoot = FXMLLoader.load(getClass().getResource("/fxml/admindashboard.fxml"));

            Scene scene = new Scene(homeRoot, 900, 600);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
