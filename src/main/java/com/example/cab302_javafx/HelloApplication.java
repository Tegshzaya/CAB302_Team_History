package com.example.cab302_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static HelloApplication instance;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        this.primaryStage = stage;

        // Start with the login screen
        loadScene("Login.fxml");

        stage.setTitle("Historical Art Guesser");
        stage.show();
    }

    public void loadScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    public static HelloApplication getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}