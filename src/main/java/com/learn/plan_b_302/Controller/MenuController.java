package com.learn.plan_b_302.Controller;


import com.learn.plan_b_302.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


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
        navigate(event, "/fxml/Year.fxml", controller -> {});
    }


    // Login FXML
    @FXML
    protected void ReturnLogin(ActionEvent event) {
        navigate(event, "/fxml/Login.fxml", controller -> {});
    }
}
