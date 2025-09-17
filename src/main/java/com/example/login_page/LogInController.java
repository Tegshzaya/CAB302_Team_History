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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.IOException;

public class LogInController extends BaseController{

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


        // Check if user can exist
        Connection connection = DatabaseConnection.getInstance();

        try {
            if (DatabaseConnection.checkUser(connection,u, p)) {
                navigate(event,"SelectionScreen.fxml", controller -> {});
                connection.close();
            } else {
                showInfo("Login Failed","Invalid username or password");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Loading method for the signup page
    @FXML
    protected void onSignup(ActionEvent event) throws java.io.IOException {
        navigate(event,"Signup.fxml",controller -> {});
    }
}
