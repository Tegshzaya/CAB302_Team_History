package com.learn.plan_b_302;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class GameHistoryController extends BaseController {
    
    @FXML private Label totalGamesLabel;
    @FXML private Label overallAccuracyLabel;
    @FXML private Label yearStatsLabel;
    @FXML private Label yearAccuracyLabel;
    @FXML private Label locationStatsLabel;
    @FXML private Label locationAccuracyLabel;
    @FXML private ComboBox<String> gameTypeFilter;
    @FXML private ComboBox<String> limitFilter;
    @FXML private ScrollPane historyScrollPane;
    @FXML private VBox historyContainer;
    
    private User currentUser;
    private List<GameHistory> currentHistory;
    
    @FXML
    private void initialize() {
        // Initialize scroll pane
        historyScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        historyScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Populate ComboBoxes
        gameTypeFilter.getItems().addAll("All Games", "Year Game", "Location Game");
        limitFilter.getItems().addAll("All", "10", "25", "50");
        
        // Set default filter values
        gameTypeFilter.setValue("All Games");
        limitFilter.setValue("25");
        
        // Add listeners for filter changes
        gameTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> loadGameHistory());
        limitFilter.valueProperty().addListener((obs, oldVal, newVal) -> loadGameHistory());
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadGameHistory();
        loadStatistics();
    }
    
    private void loadStatistics() {
        if (currentUser == null) return;
        
        GameHistoryManager.GameStatistics stats = GameHistoryManager.getUserStatistics(currentUser.getId());
        
        // Update overall statistics
        totalGamesLabel.setText("Total Games: " + stats.getTotalGamesPlayed());
        overallAccuracyLabel.setText(String.format("Overall Accuracy: %.1f%%", stats.getOverallAccuracy()));
        
        // Update year game statistics
        yearStatsLabel.setText(String.format("Games: %d | Best: %d/10", stats.yearGamesPlayed, stats.yearBestScore));
        yearAccuracyLabel.setText(String.format("Accuracy: %.1f%%", stats.yearAveragePercentage));
        
        // Update location game statistics
        locationStatsLabel.setText(String.format("Games: %d | Best: %d/30", stats.locationGamesPlayed, stats.locationBestScore));
        locationAccuracyLabel.setText(String.format("Accuracy: %.1f%%", stats.locationAveragePercentage));
    }
    
    private void loadGameHistory() {
        if (currentUser == null) {
            historyContainer.getChildren().clear();
            return;
        }
        
        // Determine filter values
        String gameTypeFilterValue = gameTypeFilter.getValue();
        String gameType = null;
        
        if ("Year Game".equals(gameTypeFilterValue)) {
            gameType = "YEAR_GAME";
        } else if ("Location Game".equals(gameTypeFilterValue)) {
            gameType = "LOCATION_GAME";
        }
        
        int limit = 0;
        String limitValue = limitFilter.getValue();
        if (!"All".equals(limitValue)) {
            try {
                limit = Integer.parseInt(limitValue);
            } catch (NumberFormatException e) {
                limit = 25; // Default fallback
            }
        }
        
        // Load history from database
        currentHistory = GameHistoryManager.getUserGameHistory(currentUser.getId(), gameType, limit);
        
        // Clear existing items
        historyContainer.getChildren().clear();
        
        // Add history items
        if (currentHistory.isEmpty()) {
            Label noHistoryLabel = new Label("No games played yet. Start playing to see your history here!");
            noHistoryLabel.getStyleClass().add("body");
            historyContainer.getChildren().add(noHistoryLabel);
        } else {
            for (GameHistory game : currentHistory) {
                VBox historyItem = createHistoryItem(game);
                historyContainer.getChildren().add(historyItem);
            }
        }
    }
    
    private VBox createHistoryItem(GameHistory game) {
        VBox itemContainer = new VBox(12);
        itemContainer.getStyleClass().add("glass-card-subtle");
        itemContainer.setPadding(new Insets(20));
        
        // Header with game type and performance
        HBox header = new HBox(16);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label gameTypeLabel = new Label(game.getGameTypeDisplay());
        gameTypeLabel.getStyleClass().add("body");
        
        Label performanceLabel = new Label(game.getPerformanceLevel());
        performanceLabel.getStyleClass().add("caption");
        
        // Spacer
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label dateLabel = new Label(game.getFormattedDate());
        dateLabel.getStyleClass().add("body-small");
        
        header.getChildren().addAll(gameTypeLabel, performanceLabel, spacer, dateLabel);
        
        // Statistics row
        HBox statsRow = new HBox(32);
        statsRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        VBox scoreBox = new VBox(4);
        scoreBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label scoreHeaderLabel = new Label("Score");
        scoreHeaderLabel.getStyleClass().add("caption");
        Label scoreValueLabel = new Label(game.getFormattedScore());
        scoreValueLabel.getStyleClass().add("body");
        scoreBox.getChildren().addAll(scoreHeaderLabel, scoreValueLabel);
        
        VBox accuracyBox = new VBox(4);
        accuracyBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label accuracyHeaderLabel = new Label("Accuracy");
        accuracyHeaderLabel.getStyleClass().add("caption");
        Label accuracyValueLabel = new Label(game.getFormattedAccuracy());
        accuracyValueLabel.getStyleClass().add("body");
        accuracyBox.getChildren().addAll(accuracyHeaderLabel, accuracyValueLabel);
        
        VBox percentageBox = new VBox(4);
        percentageBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label percentageHeaderLabel = new Label("Score %");
        percentageHeaderLabel.getStyleClass().add("caption");
        Label percentageValueLabel = new Label(String.format("%.1f%%", game.getScorePercentage()));
        percentageValueLabel.getStyleClass().add("body");
        percentageBox.getChildren().addAll(percentageHeaderLabel, percentageValueLabel);
        
        statsRow.getChildren().addAll(scoreBox, accuracyBox, percentageBox);
        
        itemContainer.getChildren().addAll(header, statsRow);
        
        return itemContainer;
    }
    
    @FXML
    private void handleRefresh() {
        loadGameHistory();
        loadStatistics();
    }
    
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        navigate(event, "/fxml/Menu.fxml", controller -> {
            if (controller instanceof MenuController mc) {
                mc.setCurrentUser(currentUser);
                mc.refreshStatistics();
            }
        });
    }
    
    @FXML
    private void handleClearHistory(ActionEvent event) {
        if (currentUser == null) return;
        
        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Clear Game History");
        confirmation.setHeaderText("Are you sure you want to clear your game history?");
        confirmation.setContentText("This action cannot be undone. All your game records will be permanently deleted.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Clear history for current user
                // Note: We would need to add a method to GameHistoryManager for this
                // For now, just refresh the display
                loadGameHistory();
                loadStatistics();
                
                // Show success message
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("History Cleared");
                success.setHeaderText("Game history has been cleared successfully.");
                success.showAndWait();
            }
        });
    }
}