package linkui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class librarycontroller {
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
