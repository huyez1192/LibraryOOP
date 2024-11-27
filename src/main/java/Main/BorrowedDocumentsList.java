package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class BorrowedDocumentsList extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải file FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/borrowedDocuments.fxml")));

        primaryStage.setTitle("Borrowed Books");
        primaryStage.setScene(new Scene(root, 900, 600)); // Kích thước cửa sổ
        primaryStage.show();
    }

    public static void main (String[] args) {
        launch(args); // Bắt đầu ứng dụng JavaFX
    }
}