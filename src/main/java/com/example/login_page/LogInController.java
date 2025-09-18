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
        System.out.println("LogInController: Login button clicked");

        // Get user input
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        System.out.println("LogInController: Attempting login for user: " + username);

        if (UserStore.validate(username, password)) {
            System.out.println("LogInController: Login successful, navigating to selection screen");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectionScreen.fxml"));
            Scene selectionScreen = new Scene(loader.load(), 800, 700);
            selectionScreen.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

            // Use the primary stage instead of creating new ones
            Stage stage = HelloApplication.getPrimaryStage();
            if (stage == null) {
                System.err.println("LogInController: Primary stage is null, falling back to event source");
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }

            stage.setTitle("Historical Art Guesser - Main Menu");
            stage.setScene(selectionScreen);
        } else {
            System.out.println("LogInController: Login failed - invalid credentials");
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Login Failed");
            err.setHeaderText(null);
            err.setContentText("Invalid username or password. Please try again.");
            err.showAndWait();
        }
    }

    // Loading method for the signup page
    @FXML
    protected void onSignup(ActionEvent event) throws java.io.IOException {
        System.out.println("LogInController: Signup button clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
        Scene signupScene = new Scene(loader.load(), 800, 700);
        signupScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        // Use the primary stage instead of creating new ones
        Stage stage = HelloApplication.getPrimaryStage();
        if (stage == null) {
            System.err.println("LogInController: Primary stage is null, falling back to event source");
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        }
        
        stage.setTitle("Sign Up - Historical Art Guesser");
        stage.setScene(signupScene);
    }
}
