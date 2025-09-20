package com.learn.plan_b_302.Controller;
import com.learn.plan_b_302.User;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController{

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private DatabaseManager dbManager = new DatabaseManager();
    protected static User currentUser;

    @FXML
    protected void handleLogin(ActionEvent event) {
        System.out.println("Handling login");
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        User user = dbManager.authenticateUser(username, password);
        if (user != null) {
            showInfo("Login", "Login Successful");
            currentUser = user;
            navigate(event, "/fxml/Menu.fxml",controller -> {});
        } else {
            showError("Invalid username or password");
        }
    }
    @FXML
    protected void switchToSignup(ActionEvent event) {
        System.out.println("Switching to Signup...");
        navigate(event, "/fxml/Signup.fxml", controller -> {});
    }
}
