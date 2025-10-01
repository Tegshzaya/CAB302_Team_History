package com.learn.plan_b_302;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public abstract class BaseController {

    /** Utility method for switching between different FXML screens */
    protected void navigate(ActionEvent event, String fxmlPath, ControllerConsumer controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxmlPath),
                            "Missing FXML: " + fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controllerConsumer != null && controller != null) {
                controllerConsumer.accept(controller);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility method to show an error popup
    protected void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Sign Up Error");
        a.setHeaderText(null); // no header, only content
        a.setContentText(message);
        a.showAndWait();
    }


    // Utility method to show an information popup
    protected void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    /** Functional interface for passing controller setup logic */
    @FunctionalInterface
    protected interface ControllerConsumer {
        void accept(Object controller);
    }
}
