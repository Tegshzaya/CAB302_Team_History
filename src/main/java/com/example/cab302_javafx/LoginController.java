package com.example.cab302_javafx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter both email and password.");
            return;
        }

        // TODO: Add actual authentication logic here
        System.out.println("Login attempt with email: " + email);
        // If login is successful, you can switch to the main application view
    }

    @FXML
    private void goToSignup() throws IOException {
        // This will switch the scene to the sign-up page
        HelloApplication.getInstance().loadScene("SignUp.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}