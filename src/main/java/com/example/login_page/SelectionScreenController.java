package com.example.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SelectionScreenController {
    
    @FXML
    public void initialize() {
        System.out.println("SelectionScreenController: Initialized successfully");
    }
    
    @FXML
    protected void onChooseLocationGame(ActionEvent event) {
        System.out.println("SelectionScreenController: Location game button clicked - navigating to GuessLocation.fxml");
        navigate(event, "GuessLocation.fxml", controller -> {
        });
    }

    @FXML
    protected void onChooseYearGame(ActionEvent event) {
        // TODO: replace with actual Year game FXML
        navigate(event, "GuessYear.fxml", controller -> {
        });
    }

    @FXML
    protected void onSignOut(ActionEvent event) {
        System.out.println("SelectionScreenController: Sign out button clicked - navigating to Login.fxml");
        navigate(event, "Login.fxml", controller -> {});
    }

    // Utility method for switching between different FXML screens
    private void navigate(ActionEvent event, String fxmlPath, ControllerConsumer controllerConsumer) {
        try {
            System.out.println("SelectionScreenController: Navigating to " + fxmlPath);
            // Load the requested FXML file
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxmlPath),
                            "Missing FXML: " + fxmlPath));
            Parent root = loader.load();
            System.out.println("SelectionScreenController: Successfully loaded " + fxmlPath);

            // Get the controller of the new FXML and pass data if needed
            Object controller = loader.getController();
            if (controllerConsumer != null && controller != null) {
                controllerConsumer.accept(controller);
            }

            // Use the primary stage instead of creating new ones
            Stage stage = HelloApplication.getPrimaryStage();
            if (stage == null) {
                System.err.println("SelectionScreenController: Primary stage is null, falling back to event source");
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }
            
            Scene newScene = new Scene(root);
            
            // Apply the stylesheet to the new scene
            newScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            System.out.println("SelectionScreenController: Setting new scene on stage");
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
            // Shows the console errors for developers, can be removed in end product or replaced with user-friendly message
        }
    }

    /** Small functional interface to avoid importing java.util.function just for one method and to make app lighter. */
    private interface ControllerConsumer {
        void accept(Object controller);
    }
}
