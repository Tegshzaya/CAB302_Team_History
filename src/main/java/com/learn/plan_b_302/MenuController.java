package com.learn.plan_b_302;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import javax.xml.stream.Location;
import java.io.IOException;


public class MenuController extends BaseController{

    @FXML private Label welcomeLabel;
    @FXML private Label yearBestScoreLabel;
    @FXML private Label yearGamesPlayedLabel;
    @FXML private Label locationBestScoreLabel;
    @FXML private Label locationGamesPlayedLabel;
    @FXML private VBox recentGamesContainer;

    private User currentUser;  // store the logged-in user here

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }
    
    /**
     * Refresh the user statistics - call this when returning from games
     */
    public void refreshStatistics() {
        updateUserInfo();
    }
    
    private void updateRecentGames() {
        System.out.println("MenuController.updateRecentGames() called");
        System.out.println("  currentUser: " + (currentUser != null ? currentUser.getUsername() + " (ID: " + currentUser.getId() + ")" : "null"));
        System.out.println("  recentGamesContainer: " + (recentGamesContainer != null ? "OK" : "null"));
        
        if (currentUser == null || recentGamesContainer == null) {
            System.out.println("  Skipping recent games update - user or container is null");
            return;
        }
        
        // Clear existing content
        recentGamesContainer.getChildren().clear();
        
        // Get recent games (last 5 games)
        java.util.List<UserGameRecord> recentGames = UserGameManager.getRecentGames(currentUser.getId(), 5);
        System.out.println("  Found " + recentGames.size() + " recent games");
        
        if (recentGames.isEmpty()) {
            Label noGamesLabel = new Label("No games played yet. Start playing to see your recent games here!");
            noGamesLabel.getStyleClass().add("body-small");
            recentGamesContainer.getChildren().add(noGamesLabel);
            System.out.println("  Added 'no games' message");
        } else {
            for (UserGameRecord game : recentGames) {
                System.out.println("  Adding game: " + game.getGameTypeDisplay() + " - " + game.getScoreDisplay() + " on " + game.getFormattedDate());
                HBox gameItem = createRecentGameItem(game);
                recentGamesContainer.getChildren().add(gameItem);
            }
            System.out.println("  Added " + recentGames.size() + " game items to container");
        }
    }
    
    private HBox createRecentGameItem(UserGameRecord game) {
        HBox gameItem = new HBox(16);
        gameItem.getStyleClass().add("glass-card-subtle");
        gameItem.setPadding(new Insets(12, 16, 12, 16));
        gameItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label gameTypeLabel = new Label(game.getGameTypeDisplay());
        gameTypeLabel.getStyleClass().add("body");
        
        Label scoreLabel = new Label("Score: " + game.getScoreDisplay());
        scoreLabel.getStyleClass().add("body");
        
        // Spacer
        javafx.scene.layout.Pane spacer = new javafx.scene.layout.Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label dateLabel = new Label(game.getFormattedDate());
        dateLabel.getStyleClass().add("body-small");
        
        gameItem.getChildren().addAll(gameTypeLabel, scoreLabel, spacer, dateLabel);
        
        return gameItem;
    }
    
    @FXML
    private void initialize() {
        // If no user is set directly, try to get from UserManager
        if (currentUser == null) {
            currentUser = UserManager.getCurrentUser();
        }
        updateUserInfo();
    }
    
    private void updateUserInfo() {
        if (currentUser != null) {
            // Update welcome message
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            
            // Get simple statistics from user games
            int yearBestScore = UserGameManager.getBestScore(currentUser.getId(), "YEAR_GAME");
            int yearGamesPlayed = UserGameManager.getGamesPlayed(currentUser.getId(), "YEAR_GAME");
            int locationBestScore = UserGameManager.getBestScore(currentUser.getId(), "LOCATION_GAME");
            int locationGamesPlayed = UserGameManager.getGamesPlayed(currentUser.getId(), "LOCATION_GAME");
            
            // Update Year Game stats with real data
            yearBestScoreLabel.setText("Best: " + yearBestScore + "/10");
            yearGamesPlayedLabel.setText("Played: " + yearGamesPlayed);
            
            // Update Location Game stats with real data
            locationBestScoreLabel.setText("Best: " + locationBestScore + "/30");
            locationGamesPlayedLabel.setText("Played: " + locationGamesPlayed);
            
            // Update recent games
            updateRecentGames();
        } else {
            // Default values when no user is logged in
            welcomeLabel.setText("Welcome, Player!");
            yearBestScoreLabel.setText("Best: 0/10");
            yearGamesPlayedLabel.setText("Played: 0");
            locationBestScoreLabel.setText("Best: 0/30");
            locationGamesPlayedLabel.setText("Played: 0");
        }
    }

    // Location game FXML
    @FXML
    protected void handleLocationGame(ActionEvent event){
        navigate(event, "/fxml/Location.fxml", controller -> {
            if (controller instanceof LocationController lc) {
                lc.setCurrentUser(currentUser);  // pass the user to LocationController
            }
        });
    }

    // Year game FXML
    @FXML
    protected void handleYearGame(ActionEvent event) {
        navigate(event, "/fxml/game.fxml", controller -> {
            if (controller instanceof GameController gc) {
                gc.setCurrentUser(currentUser);  // <-- critical  // pass the user into GameController
                // Also start game if user was already set from UserManager
                gc.startGameIfUserReady();
            }
        });
    }


    // View History handler
    @FXML
    protected void handleViewHistory(ActionEvent event) {
        navigate(event, "/fxml/game-history.fxml", controller -> {
            if (controller instanceof GameHistoryController ghc) {
                ghc.setCurrentUser(currentUser);
            }
        });
    }
    
    // Sign Out handler
    @FXML
    protected void handleSignOut(ActionEvent event) {
        // Clear current user
        currentUser = null;
        UserManager.clearCurrentUser();
        
        // Navigate back to login screen
        navigate(event, "/fxml/login.fxml", controller -> {});
    }
    
    // Keep for backward compatibility (if called from old UI elements)
    @FXML
    protected void ReturnLogin(ActionEvent event) {
        handleSignOut(event);
    }
}
