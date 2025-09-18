package com.example.login_page;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("HelloApplication: Starting application...");

        // Ensure only one stage is created
        if (primaryStage != null) {
            System.out.println("HelloApplication: Warning - Primary stage already exists!");
            return;
        }

        primaryStage = stage;

        // Start with the login screen instead of selection screen
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        System.out.println("HelloApplication: Loading Login.fxml...");
        Scene scene = new Scene(fxmlLoader.load(), 800, 700);
        System.out.println("HelloApplication: Scene created successfully");

        primaryStage.setTitle("Historical Art Guesser - Login");
        scene.getStylesheets().add(HelloApplication.
                class.getResource("styles.css").toExternalForm()); // adding the CSS

        System.out.println("HelloApplication: Showing stage...");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("HelloApplication: Application startup complete");
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}