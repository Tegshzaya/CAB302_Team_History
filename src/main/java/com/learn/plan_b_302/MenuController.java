package com.learn.plan_b_302;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.stream.Location;
import java.io.IOException;


public class MenuController extends BaseController{

    private User currentUser;

    // Location game FXML
    @FXML
    protected void handleLocationGame(ActionEvent event){
        navigate(event, "fxml/Location.fxml",controller -> {});
    }

    // Year game FXML
    @FXML
    protected void handleYearGame(ActionEvent event) {
        openGameScene(currentUser);
    }
    protected void openGameScene(User user) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            GameController gameController = loader.getController();
            gameController.setCurrentUser(user);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Login FXML
    @FXML
    protected void ReturnLogin(ActionEvent event) {
        navigate(event,"fxml/login.fxml",controller -> {});
    }
}
