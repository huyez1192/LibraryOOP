package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class giaodien extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));
                Scene scene = new Scene(root, 400, 600);
                primaryStage.centerOnScreen();
                primaryStage.setScene(scene);
                primaryStage.show();
        }

        public static void main(String[] args) {
                launch(args);
        }
}