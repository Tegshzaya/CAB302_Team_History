package com.example.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {

    // Text fields for entering the user info
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    protected void onLogin(ActionEvent event) throws java.io.IOException {
        // String u = username
        // String p = password
        // Check if the user exists in the database, if the field is empty safe it as ""
        String u = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String p = passwordField.getText() == null ? "" : passwordField.getText();
        if (UserStore.validate(u, p)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectionScreen.fxml"));
            Scene selectionScreen = new Scene(loader.load(), 450, 450);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Selection Screen");
            stage.setScene(selectionScreen);
            stage.show();
        } else {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Login Failed");
            err.setHeaderText(null);
            err.setContentText("Invalid username or password.");
            err.showAndWait();
        }
    }

    // Loading method for the signup page
    @FXML
    protected void onSignup(ActionEvent event) throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
        Scene signupScene = new Scene(loader.load(), 450, 450);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Sign Up");
        stage.setScene(signupScene);
        stage.show();
    }
}
