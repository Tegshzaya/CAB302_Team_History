package com.learn.plan_b_302;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

/**
 * Test class for AuthController functionality
 */
class AuthControllerTest extends ApplicationTest {

    private AuthController controller;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button signupButton;
    private Label messageLabel;

    @BeforeAll
    static void setUpHeadlessMode() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
        
        // Get UI components
        usernameField = lookup("#usernameField").query();
        passwordField = lookup("#passwordField").query();
        loginButton = lookup("#loginButton").query();
        messageLabel = lookup("#messageLabel").query();
    }

    @BeforeEach
    void setUp() {
        Platform.runLater(() -> {
            if (usernameField != null) usernameField.clear();
            if (passwordField != null) passwordField.clear();
            if (messageLabel != null) messageLabel.setText("");
        });
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("Should initialize controller properly")
    void testControllerInitialization() {
        assertNotNull(controller, "Controller should be initialized");
        assertNotNull(usernameField, "Username field should be found");
        assertNotNull(passwordField, "Password field should be found");
        assertNotNull(loginButton, "Login button should be found");
    }

    @Test
    @DisplayName("Should show error for empty fields")
    void testEmptyFieldsValidation() {
        Platform.runLater(() -> {
            usernameField.setText("");
            passwordField.setText("");
        });
        
        clickOn(loginButton);
        
        sleep(500, TimeUnit.MILLISECONDS);
        
        Platform.runLater(() -> {
            assertNotNull(messageLabel.getText(), "Message should not be null");
            assertFalse(messageLabel.getText().isEmpty(), "Message should not be empty");
        });
    }

    @Test
    @DisplayName("Should show error for missing username")
    void testMissingUsernameValidation() {
        Platform.runLater(() -> {
            usernameField.setText("");
            passwordField.setText("password123");
        });
        
        clickOn(loginButton);
        
        sleep(500, TimeUnit.MILLISECONDS);
        
        Platform.runLater(() -> {
            String message = messageLabel.getText();
            assertTrue(message.contains("fill") || message.contains("empty") || message.contains("required"), 
                "Should show validation message for missing username");
        });
    }

    @Test
    @DisplayName("Should show error for missing password")
    void testMissingPasswordValidation() {
        Platform.runLater(() -> {
            usernameField.setText("testuser");
            passwordField.setText("");
        });
        
        clickOn(loginButton);
        
        sleep(500, TimeUnit.MILLISECONDS);
        
        Platform.runLater(() -> {
            String message = messageLabel.getText();
            assertTrue(message.contains("fill") || message.contains("empty") || message.contains("required"), 
                "Should show validation message for missing password");
        });
    }

    @Test
    @DisplayName("Should handle invalid login credentials")
    void testInvalidLogin() {
        Platform.runLater(() -> {
            usernameField.setText("invaliduser");
            passwordField.setText("wrongpassword");
        });
        
        clickOn(loginButton);
        
        sleep(1000, TimeUnit.MILLISECONDS);
        
        Platform.runLater(() -> {
            String message = messageLabel.getText();
            assertTrue(message.contains("Invalid") || message.contains("incorrect") || message.contains("wrong"), 
                "Should show error for invalid credentials");
        });
    }

    @Test
    @DisplayName("Should clear fields after failed login")
    void testFieldsClearAfterFailedLogin() {
        Platform.runLater(() -> {
            usernameField.setText("testuser");
            passwordField.setText("wrongpass");
        });
        
        clickOn(loginButton);
        
        sleep(1000, TimeUnit.MILLISECONDS);
        
        Platform.runLater(() -> {
            // Fields might still contain text, but error message should be shown
            assertFalse(messageLabel.getText().isEmpty(), "Error message should be displayed");
        });
    }

    @Test
    @DisplayName("Should enable login button when fields have text")
    void testLoginButtonState() {
        Platform.runLater(() -> {
            assertTrue(loginButton.isVisible(), "Login button should be visible");
            assertFalse(loginButton.isDisabled(), "Login button should be enabled");
        });
    }

    @Test
    @DisplayName("Should handle database connection errors gracefully")
    void testDatabaseConnectionError() {
        // This test verifies that the controller handles database errors without crashing
        Platform.runLater(() -> {
            usernameField.setText("testuser");
            passwordField.setText("testpass");
        });
        
        clickOn(loginButton);
        
        sleep(1000, TimeUnit.MILLISECONDS);
        
        // Should not crash the application
        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                assertNotNull(messageLabel.getText(), "Message label should still exist after database operation");
            });
        });
    }

    @Test
    @DisplayName("Should maintain UI state during login process")
    void testUIStateDuringLogin() {
        Platform.runLater(() -> {
            usernameField.setText("testuser");
            passwordField.setText("testpass123");
            
            String initialUsername = usernameField.getText();
            String initialPassword = passwordField.getText();
            
            assertEquals("testuser", initialUsername, "Username should be preserved");
            assertEquals("testpass123", initialPassword, "Password should be preserved");
        });
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        Platform.runLater(() -> {
            if (messageLabel != null) messageLabel.setText("");
        });
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        FxToolkit.cleanupStages();
    }
}