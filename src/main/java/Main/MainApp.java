package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import javafx.geometry.Rectangle2D;
import java.util.Objects;
import java.util.Scanner;


public class MainApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));
                Scene scene = new Scene(root, 400, 600);
                primaryStage.centerOnScreen();
                primaryStage.setScene(scene);
                primaryStage.show();

//                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/library.fxml")));
//                Scene scene = new Scene(root);
//                Rectangle2D rectangle = Screen.getPrimary().getVisualBounds();
//                primaryStage.setWidth(rectangle.getWidth());
//                primaryStage.setHeight(rectangle.getHeight()
//
//                );
                primaryStage.centerOnScreen();
                primaryStage.setScene(scene);
                primaryStage.show();

        }

        public static void main(String[] args) {
                launch(args);
        }
}

