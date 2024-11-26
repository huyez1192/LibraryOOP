package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class LibraryController {
    @FXML
    private VBox overlay;

    @FXML
    private void showOverlay() {
        overlay.setVisible(true);
    }

    @FXML
    private void hideOverlay() {
        overlay.setVisible(false);
    }
}
