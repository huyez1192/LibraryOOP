package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

public class HoverOverlayController {

    @FXML
    private VBox overlay1;

    @FXML
    private VBox overlay2;

    @FXML
    private VBox overlay3;

    @FXML
    public void showOverlay(MouseEvent event) {
        VBox overlay = (VBox) ((VBox) event.getSource()).getChildren().get(1);
        overlay.setVisible(true);
    }

    @FXML
    public void hideOverlay(MouseEvent event) {
        VBox overlay = (VBox) ((VBox) event.getSource()).getChildren().get(1);
        overlay.setVisible(false);
    }
}
