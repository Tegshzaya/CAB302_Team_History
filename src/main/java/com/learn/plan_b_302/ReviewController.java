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

public class ReviewController extends BaseController {
    
    @FXML private Label summaryLabel;
    @FXML private Label mistakesLabel;
    @FXML private Label correctLabel;
    @FXML private Label incorrectLabel;
    @FXML private Label percentageLabel;
    @FXML private Label accuracyLabel;
    @FXML private ScrollPane reviewScrollPane;
    @FXML private VBox reviewContainer;
    
    private List<ReviewResult> reviewResults;
    private User currentUser;
    
    @FXML
    private void initialize() {
        // Initialize scroll pane
        reviewScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        reviewScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }
    
    public void setReviewData(List<ReviewResult> results, User user) {
        this.reviewResults = results;
        this.currentUser = user;
        displayReview();
    }
    
    private void displayReview() {
        if (reviewResults == null || reviewResults.isEmpty()) {
            return;
        }
        
        int totalQuestions = reviewResults.size();
        int correctCount = 0;
        int incorrectCount = 0;
        
        // Count correct and incorrect answers
        for (ReviewResult result : reviewResults) {
            if (result.isCorrect()) {
                correctCount++;
            } else {
                incorrectCount++;
            }
        }
        
        // Update summary labels
        summaryLabel.setText("Total: " + totalQuestions + " questions");
        mistakesLabel.setText("Mistakes: " + incorrectCount);
        correctLabel.setText("✅ Correct: " + correctCount);
        incorrectLabel.setText("❌ Incorrect: " + incorrectCount);
        
        double percentage = totalQuestions == 0 ? 0 : (double) correctCount / totalQuestions * 100.0;
        percentageLabel.setText(String.format("%.1f%%", percentage));
        accuracyLabel.setText("Accuracy");
        
        // Clear existing items
        reviewContainer.getChildren().clear();
        
        // Add review items
        for (ReviewResult result : reviewResults) {
            VBox reviewItem = createReviewItem(result);
            reviewContainer.getChildren().add(reviewItem);
        }
    }
    
    private VBox createReviewItem(ReviewResult result) {
        VBox itemContainer = new VBox(16);
        itemContainer.getStyleClass().add("glass-card");
        itemContainer.setPadding(new Insets(24));
        
        // Header with question number and result status
        HBox header = new HBox(16);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label questionLabel = new Label("Question " + result.getQuestionNumber());
        questionLabel.getStyleClass().add("body");
        
        Label statusLabel = new Label(result.isCorrect() ? "✅ Correct" : "❌ Incorrect");
        statusLabel.getStyleClass().add(result.isCorrect() ? "correct-feedback" : "wrong-feedback");
        
        header.getChildren().addAll(questionLabel, statusLabel);
        
        // Artwork section
        HBox contentBox = new HBox(24);
        contentBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        // Image
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
            
            Label titleLabel = new Label(result.getArtworkTitle());
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
        
        // Answer information
        VBox answerInfo = new VBox(8);
        
        if (!result.isCorrect()) {
            Label yourAnswerLabel = new Label("Your answer: " + result.getUserAnswer());
            yourAnswerLabel.getStyleClass().add("wrong-feedback");
            answerInfo.getChildren().add(yourAnswerLabel);
        }
        
        Label correctAnswerLabel = new Label("Correct answer: " + result.getCorrectAnswer());
        correctAnswerLabel.getStyleClass().add("correct-feedback");
        answerInfo.getChildren().add(correctAnswerLabel);
        
        // Historical context
        Label contextHeader = new Label("📚 Historical Context");
        contextHeader.getStyleClass().add("caption");
        
        Label contextText = new Label(result.getHistoricalContext());
        contextText.getStyleClass().add("body");
        contextText.setWrapText(true);
        contextText.setMaxWidth(580);
        
        infoBox.getChildren().addAll(answerInfo, contextHeader, contextText);
        
        contentBox.getChildren().addAll(imageBox, infoBox);
        
        itemContainer.getChildren().addAll(header, contentBox);
        
        return itemContainer;
    }
    
    @FXML
    private void handlePlayAgain(ActionEvent event) {
        navigate(event, "/fxml/game.fxml", controller -> {
            if (controller instanceof GameController gc) {
                gc.setCurrentUser(currentUser);
            }
        });
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
}