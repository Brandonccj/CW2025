package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main application entry point for TetrisJFX.
 * Initializes JavaFX application, loads custom fonts, and displays the main menu.
 */

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Font.loadFont(getClass().getClassLoader().getResource("determination.ttf").toExternalForm(), 38);

        Parent root = FXMLLoader.load(getClass().getResource("/mainMenu.fxml"));

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 620, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
