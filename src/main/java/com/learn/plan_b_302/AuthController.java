package com.learn.plan_b_302;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class AuthController extends BaseController{
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button switchToSignupButton;
    @FXML private Button signupButton;
    @FXML private Button switchToLoginButton;
    @FXML private Label messageLabel;

    private DatabaseManager dbManager = new DatabaseManager();

    @FXML
    private void initialize() {
        // Initialize database
        dbManager.initializeDatabase();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields", false);
            return;
        }

        User user = dbManager.authenticateUser(username, password);
        if (user != null) {
            showMessage("Login successful!", true);
            openGameScene(user);
        } else {
            showMessage("Invalid username or password", false);
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields", false);
            return;
        }

        if (username.length() < 3) {
            showMessage("Username must be at least 3 characters", false);
            return;
        }

        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters", false);
            return;
        }

        if (dbManager.createUser(username, password)) {
            showMessage("Signup successful! Please login.", true);
            navigate(event, "fxml/login.fxml",controller -> {});
        } else {
            showMessage("Username already exists", false);
        }
    }

    @FXML
    private void switchToSignup() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void showMessage(String message, boolean isSuccess) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add(isSuccess ? "success-message" : "error-message");
        }
    }
}