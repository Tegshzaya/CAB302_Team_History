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
    protected void onChooseLocationGame(ActionEvent event) {
        // TODO: replace with actual Location game FXML
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
        // Back to Login screen
        navigate(event, "Login.fxml", controller -> {});
    }

    // Utility method for switching between different FXML screens
    private void navigate(ActionEvent event, String fxmlPath, ControllerConsumer controllerConsumer) {
        try {
            // Load the requested FXML file
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxmlPath),
                            "Missing FXML: " + fxmlPath));
            Parent root = loader.load();

            // Get the controller of the new FXML and pass data if needed
            Object controller = loader.getController();
            if (controllerConsumer != null && controller != null) {
                controllerConsumer.accept(controller);
            }

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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
