package com.learn.plan_b_302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController extends BaseController{
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private DatabaseManager dbManager = new DatabaseManager();
    @FXML
    public void handleSignup(ActionEvent event) {
        System.out.println("Handling Signup");
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (dbManager.createUser(username, password)) {
            showError("Signup successful! Please login.");
            navigate(event, "/fxml/Login.fxml", controller -> {});

        } else {
            showError("Username already exists");
        }
    }
    @FXML
    public void switchToLogin(ActionEvent event) {
        System.out.println("Switching to Login...");
        navigate(event, "/fxml/Login.fxml", controller -> {});
    }
}
