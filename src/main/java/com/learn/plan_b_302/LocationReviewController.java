package com.learn.plan_b_302;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

public class LocationReviewController extends BaseController {
    
    @FXML private Label summaryLabel;
    @FXML private Label totalScoreLabel;
    @FXML private Label accurateLabel;
    @FXML private Label inaccurateLabel;
    @FXML private Label averageDistanceLabel;
    @FXML private Label accuracyLabel;
    @FXML private ScrollPane reviewScrollPane;
    @FXML private VBox reviewContainer;
    
    private List<LocationReviewResult> reviewResults;
    private User currentUser;
    
    @FXML
    private void initialize() {
        // Initialize scroll pane
        reviewScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        reviewScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }
    
    public void setReviewData(List<LocationReviewResult> results, User user) {
        this.reviewResults = results;
        this.currentUser = user;
        displayReview();
    }
    
    private void displayReview() {
        if (reviewResults == null || reviewResults.isEmpty()) {
            return;
        }
        
        int totalLocations = reviewResults.size();
        int accurateCount = 0;
        int inaccurateCount = 0;
        int totalScore = 0;
        double totalDistance = 0;
        int locationsWithGuesses = 0;
        
        // Calculate statistics
        for (LocationReviewResult result : reviewResults) {
            totalScore += result.getPointsEarned();
            
            if (result.hasGuess()) {
                locationsWithGuesses++;
                totalDistance += result.getDistanceKm();
                
                if (result.isCorrect()) {
                    accurateCount++;
                } else {
                    inaccurateCount++;
                }
            } else {
                inaccurateCount++; // No guess counts as inaccurate
            }
        }
        
        // Update summary labels
        summaryLabel.setText("Total: " + totalLocations + " locations");
        totalScoreLabel.setText("Score: " + totalScore + "/30"); // Max 3 points per location * 10 locations
        accurateLabel.setText("🎯 Accurate: " + accurateCount);
        inaccurateLabel.setText("❌ Inaccurate: " + inaccurateCount);
        
        double averageDistance = locationsWithGuesses > 0 ? totalDistance / locationsWithGuesses : 0;
        averageDistanceLabel.setText(String.format("Avg: %.1f km", averageDistance));
        accuracyLabel.setText("Geography Skills");
        
        // Clear existing items
        reviewContainer.getChildren().clear();
        
        // Add review items
        for (LocationReviewResult result : reviewResults) {
            VBox reviewItem = createReviewItem(result);
            reviewContainer.getChildren().add(reviewItem);
        }
    }
    
    private VBox createReviewItem(LocationReviewResult result) {
        VBox itemContainer = new VBox(16);
        itemContainer.getStyleClass().add("glass-card");
        itemContainer.setPadding(new Insets(24));
        
        // Header with round number and result status
        HBox header = new HBox(16);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label roundLabel = new Label("Round " + result.getRoundNumber());
        roundLabel.getStyleClass().add("body");
        
        Label statusLabel = new Label(result.getAccuracyDescription());
        statusLabel.getStyleClass().add(result.isCorrect() ? "correct-feedback" : "wrong-feedback");
        
        header.getChildren().addAll(roundLabel, statusLabel);
        
        // Main content section
        HBox contentBox = new HBox(24);
        contentBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        // Image section
        VBox imageBox = new VBox(12);
        imageBox.setAlignment(javafx.geometry.Pos.CENTER);
        imageBox.setMinWidth(200);
        imageBox.setMaxWidth(200);
        
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(result.getImagePath())));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(180);
            imageView.setFitHeight(135);
            imageView.setPreserveRatio(true);
            imageView.getStyleClass().add("art-image");
            
            Label titleLabel = new Label(result.getImageTitle());
            titleLabel.getStyleClass().add("caption");
            titleLabel.setWrapText(true);
            titleLabel.setMaxWidth(180);
            
            imageBox.getChildren().addAll(imageView, titleLabel);
        } catch (Exception e) {
            Label errorLabel = new Label("Image not available");
            errorLabel.getStyleClass().add("body-small");
            imageBox.getChildren().add(errorLabel);
        }
        
        // Information section
        VBox infoBox = new VBox(16);
        infoBox.setMaxWidth(600);
        
        // Location information
        VBox locationInfo = new VBox(8);
        
        Label correctLocationLabel = new Label("📍 Correct Location: " + result.getLocationName());
        correctLocationLabel.getStyleClass().add("correct-feedback");
        locationInfo.getChildren().add(correctLocationLabel);
        
        if (result.hasGuess()) {
            Label distanceLabel = new Label(String.format("📏 Your guess was %.1f km away", result.getDistanceKm()));
            distanceLabel.getStyleClass().add("body");
            
            Label pointsLabel = new Label("🎯 Points earned: " + result.getPointsEarned() + "/3");
            pointsLabel.getStyleClass().add(result.isCorrect() ? "correct-feedback" : "wrong-feedback");
            
            locationInfo.getChildren().addAll(distanceLabel, pointsLabel);
        } else {
            Label noGuessLabel = new Label("❌ No guess was made");
            noGuessLabel.getStyleClass().add("wrong-feedback");
            locationInfo.getChildren().add(noGuessLabel);
        }
        
        // Location insight
        Label insightHeader = new Label("🌍 Location Insight");
        insightHeader.getStyleClass().add("caption");
        
        Label insightText = new Label(result.getLocationInsight());
        insightText.getStyleClass().add("body");
        insightText.setWrapText(true);
        insightText.setMaxWidth(580);
        
        infoBox.getChildren().addAll(locationInfo, insightHeader, insightText);
        
        contentBox.getChildren().addAll(imageBox, infoBox);
        
        itemContainer.getChildren().addAll(header, contentBox);
        
        return itemContainer;
    }
    
    @FXML
    private void handlePlayAgain(ActionEvent event) {
        navigate(event, "/fxml/Location.fxml", controller -> {
            if (controller instanceof LocationController lc) {
                lc.setCurrentUser(currentUser);
            }
        });
    }
    
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        navigate(event, "/fxml/Menu.fxml", controller -> {
            if (controller instanceof MenuController mc) {
                mc.setCurrentUser(currentUser);
            }
        });
    }
}