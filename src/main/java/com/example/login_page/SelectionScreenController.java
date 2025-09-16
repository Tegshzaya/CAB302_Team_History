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

public class SelectionScreenController extends BaseController{
    @FXML
    protected void onChooseLocationGame(ActionEvent event) {
        // TODO: replace with actual Location game FXML
        navigate(event, "GuessByLocation.fxml", controller -> {
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

}
