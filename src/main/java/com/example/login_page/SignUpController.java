package com.example.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUpController extends BaseController {

    // -------------------------------
    // Fields connected to FXML inputs
    // -------------------------------

    // Reference to the text field where the user enters their info
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Hyperlink backToLoginLink;


    // Event handler for the Sign up button
    // Triggered when the user submits the registration form
    @FXML
    protected void onSubmit(ActionEvent event) {
        // Collect values from text fields
        // If null safe ""
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirm  = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

        // Validation rules n errors
        if (username.isEmpty()) { showError("Username is required."); return; }
        if (username.length() < 3) { showError("Username must be at least 3 characters."); return; }
        if (password.isEmpty()) { showError("Password is required."); return; }
        if (password.length() < 6) { showError("Password must be at least 6 characters."); return; }
        if (!password.equals(confirm)) { showError("Passwords do not match."); return; }
        if (UserStore.exists(username)) { showError("This username is already taken."); return; }

        // TODO: Creating to a database. Then Adding the User to it.
        Connection connection = DatabaseConnection.getInstance();
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS UserTable (" +
                    "User_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Username VARCHAR, Password VARCHAR" +
                    ")");
            DatabaseConnection.insertUser(username,password);

            connection.close();
        } catch (SQLException sqle) {
            System.err.println(sqle);
        }



        // Save the new user account
        UserStore.save(username, password);

        // Show a success message
        showInfo("Account created!", "Your account has been created.");

        // Redirect to login screen
        goToLogin(event);
    }


    // Lets user return to the login screen without signing up
    @FXML
    protected void onBackToLogin(ActionEvent event) {
        goToLogin(event);
    }



    // Helper method to load and display the login screen
    // Uses FXMLLoader to replace the current scene
    private void goToLogin(ActionEvent event) {
        navigate(event, "Login.fxml", controller -> {});
    }

}
