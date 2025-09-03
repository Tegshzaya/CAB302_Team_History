package com.example.cab302_javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        welcomeText.getStyleClass().add("welcome-text");
    }

    @FXML
    protected void onGameButtonClick() {
        welcomeText.setText("Welcome to Historical Image Game! Learn history through visual recognition!");
        welcomeText.getStyleClass().add("welcome-text");
    }
}
