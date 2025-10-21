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
                        "Missing FXML: /fxml/login.fxml"));

        Scene scene = new Scene(loader.load(), 1200, 750);

        // Add modern CSS stylesheet (with null-safety)
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/modern-style.css"),
                        "Missing CSS: /css/modern-style.css").toExternalForm());

        stage.setTitle("Historical Art Guesser");

        // Make window resizable with minimum and preferred sizes
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        
        // Set preferred size but allow maximizing
        stage.setWidth(1200);
        stage.setHeight(750);
        
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}