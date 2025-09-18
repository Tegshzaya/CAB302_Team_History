package com.learn.plan_b_302;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GameController extends BaseController{

    @FXML private Label welcomeLabel;
    @FXML private Label scoreLabel;
    @FXML private Label questionCountLabel;
    @FXML private Label bestScoreLabel;
    @FXML private Label gamesPlayedLabel;

    @FXML private VBox menuPane;
    @FXML private VBox gamePane;
    @FXML private VBox endGamePane;

    @FXML private ImageView artImageView;
    @FXML private Button option1Button;
    @FXML private Button option2Button;
    @FXML private Button option3Button;
    @FXML private Button option4Button;
    @FXML private Label feedbackLabel;
    @FXML private Button nextQuestionButton;

    @FXML private Label finalScoreLabel;
    @FXML private Label finalPercentageLabel;
    @FXML private Label newBestScoreLabel;
    @FXML private Button playAgainButton;
    @FXML private Button returnToMenuButton;
    @FXML private Button logoutButton;

    private User currentUser;
    private List<ArtImage> artImages;
    private ArtImage currentImage;
    private int currentQuestionIndex;
    private int score;
    private int totalQuestions;
    private Button[] optionButtons;

    @FXML
    private void initialize() {
        // Initialize option buttons array for easy access
        if (option1Button != null) {
            optionButtons = new Button[]{option1Button, option2Button, option3Button, option4Button};
        }

    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            bestScoreLabel.setText("Best Score: " + currentUser.getBestScore());
            gamesPlayedLabel.setText("Games Played: " + currentUser.getGamesPlayed());
        }
    }

    // Menu actions
    @FXML
    private void handleStartGame() {
        startNewGame();
    }

    @FXML
    private void handleViewScores() {
        // Refresh user data to get latest scores
        currentUser = DatabaseManager.refreshUser(currentUser.getId());
        updateUserInfo();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Your Statistics");
        alert.setHeaderText(currentUser.getUsername() + "'s Game Statistics");
        alert.setContentText(
                "Best Score: " + currentUser.getBestScore() + "\n" +
                        "Games Played: " + currentUser.getGamesPlayed() + "\n" +
                        "Average Score: " + (currentUser.getGamesPlayed() > 0 ?
                        String.format("%.1f", (double) currentUser.getHighScoreInt() / currentUser.getGamesPlayed()) : "0")
        );
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        navigate(event, "fxml/login.fxml", controller -> {});
    }

    // Game functionality
    private void startNewGame() {
        // Initialize game state
        artImages = DatabaseManager.getAllImages();
        currentQuestionIndex = 0;
        score = 0;
        totalQuestions = Math.min(artImages.size(), 10); // Limit to 10 questions

        if (artImages.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Images Found");
            alert.setHeaderText("No art images available");
            alert.setContentText("Please make sure art images are properly loaded in the database.");
            alert.showAndWait();
            return;
        }

        showGamePane();
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex >= totalQuestions || currentQuestionIndex >= artImages.size()) {
            endGame();
            return;
        }

        currentImage = artImages.get(currentQuestionIndex);

        // Update UI
        questionCountLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + totalQuestions);
        scoreLabel.setText("Score: " + score);

        // Load image
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(currentImage.getPath())));
            artImageView.setImage(image);
        } catch (Exception e) {
            // If image not found, show placeholder
            System.err.println("Image not found: " + currentImage.getPath());
            artImageView.setImage(null);
        }

        // Set up answer buttons
        List<String> options = currentImage.getAllOptions();
        for (int i = 0; i < optionButtons.length && i < options.size(); i++) {
            optionButtons[i].setText(options.get(i));
            optionButtons[i].setDisable(false);
            optionButtons[i].getStyleClass().removeAll("correct-answer", "wrong-answer");
            optionButtons[i].getStyleClass().add("option-button");
        }

        // Clear feedback and hide next button
        feedbackLabel.setText("");
        nextQuestionButton.setVisible(false);
    }

    @FXML
    private void handleOption1() {
        handleAnswerSelection(option1Button);
    }

    @FXML
    private void handleOption2() {
        handleAnswerSelection(option2Button);
    }

    @FXML
    private void handleOption3() {
        handleAnswerSelection(option3Button);
    }

    @FXML
    private void handleOption4() {
        handleAnswerSelection(option4Button);
    }

    private void handleAnswerSelection(Button selectedButton) {
        String selectedAnswer = selectedButton.getText();
        boolean isCorrect = currentImage.isCorrectAnswer(selectedAnswer);

        // Disable all buttons
        for (Button button : optionButtons) {
            button.setDisable(true);
        }

        // Style the buttons based on correctness
        if (isCorrect) {
            selectedButton.getStyleClass().removeAll("option-button");
            selectedButton.getStyleClass().add("correct-answer");
            feedbackLabel.setText("Correct! ✓");
            feedbackLabel.getStyleClass().removeAll("wrong-feedback");
            feedbackLabel.getStyleClass().add("correct-feedback");
            score++;
        } else {
            selectedButton.getStyleClass().removeAll("option-button");
            selectedButton.getStyleClass().add("wrong-answer");

            // Highlight the correct answer
            for (Button button : optionButtons) {
                if (currentImage.isCorrectAnswer(button.getText())) {
                    button.getStyleClass().removeAll("option-button");
                    button.getStyleClass().add("correct-answer");
                    break;
                }
            }

            feedbackLabel.setText("Wrong! The correct answer is: " + currentImage.getCorrectAnswer());
            feedbackLabel.getStyleClass().removeAll("correct-feedback");
            feedbackLabel.getStyleClass().add("wrong-feedback");
        }

        // Update score display
        scoreLabel.setText("Score: " + score);

        // Show next button after a short delay
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> nextQuestionButton.setVisible(true));
        pause.play();
    }

    @FXML
    private void handleNextQuestion() {
        currentQuestionIndex++;
        loadNextQuestion();
    }

    private void endGame() {
        // Update user score in database
        DatabaseManager.updateUserScore(currentUser.getId(), score, totalQuestions);

        // Refresh user data
        currentUser = DatabaseManager.refreshUser(currentUser.getId());

        // Show end game screen
        showEndGamePane();

        // Calculate percentage
        double percentage = (double) score / totalQuestions * 100;

        // Update end game labels
        finalScoreLabel.setText("Final Score: " + score + " / " + totalQuestions);
        finalPercentageLabel.setText("Percentage: " + String.format("%.1f", percentage) + "%");

        // Check if new best score - use getHighScoreInt() instead of getBestScore()
        if (score >= currentUser.getHighScoreInt()) {
            newBestScoreLabel.setText("🎉 New Best Score! 🎉");
            newBestScoreLabel.setVisible(true);
        } else {
            newBestScoreLabel.setVisible(false);
        }
    }

    @FXML
    private void handlePlayAgain() {
        startNewGame();
    }


    @FXML
    private void handleLogoutFromEnd(ActionEvent event) {
        navigate(event, "fxml/login.fxml",controller -> {});
    }



    private void showGamePane() {
        gamePane.setVisible(true);
        endGamePane.setVisible(false);
    }

    private void showEndGamePane() {
        gamePane.setVisible(false);
        endGamePane.setVisible(true);
    }
}