package com.example.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogInController {

    @FXML
    protected void onLogin() {
        // demo-only: no logic
    }

    @FXML
    protected void onSignup(ActionEvent event) throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml")); // ensure exact name/path
        Scene signupScene = new Scene(loader.load(), 520, 420);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Sign Up");
        stage.setScene(signupScene);
        stage.show();
    }
}
