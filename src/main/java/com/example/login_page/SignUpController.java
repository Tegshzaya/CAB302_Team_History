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

public class SignUpController {

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
        try {
            // Load the Login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 700);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

            // Get the current stage from the event source (the button/hyperlink)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Update the stage with the login scene
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            // If loading fails, show an error alert
            showError("Failed to open Login screen: " + e.getMessage());
        }
    }



    // Utility method to show an error popup
    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Sign Up Error");
        a.setHeaderText(null); // no header, only content
        a.setContentText(message);
        a.showAndWait();
    }


    // Utility method to show an information popup
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
