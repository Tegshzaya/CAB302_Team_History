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
        navigate(event, "/fxml/Location.fxml",controller -> {});
    }

    // Year game FXML
    @FXML
    protected void handleYearGame(ActionEvent event) {
        navigate(event,"/fxml/game.fxml",controller -> {});
    }


    // Login FXML
    @FXML
    protected void ReturnLogin(ActionEvent event) {
        navigate(event,"/fxml/login.fxml",controller -> {});
    }
}
