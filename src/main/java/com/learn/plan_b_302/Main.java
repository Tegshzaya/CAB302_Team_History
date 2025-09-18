package com.learn.plan_b_302;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/login.fxml"),
                        "Missing FXML: /fxml/game.fxml"));

        Scene scene = new Scene(loader.load(), 800, 600);

        // Add CSS stylesheet (with null-safety)
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/style.css"),
                        "Missing CSS: /css/style.css").toExternalForm());

        stage.setTitle("Art Guessing Game");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}