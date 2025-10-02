package com.learn.plan_b_302;

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

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;


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
    @FXML private Button submitAnswerButton;
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
    private boolean questionReady = false;
    private boolean gameOver = false;
    private String selectedAnswer = null;


    @FXML
    private void initialize() {
        optionButtons = new Button[]{option1Button, option2Button, option3Button, option4Button};
        for (Button b : optionButtons) b.setDisable(true);
        nextQuestionButton.setDisable(true); // Start disabled like Location game
        submitAnswerButton.setDisable(true); // Start disabled, not hidden

        System.out.println("[INIT] GameController@" + System.identityHashCode(this));

    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();

        // Start once we have the user
        startNewGame();
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            bestScoreLabel.setText("Best Score: " + currentUser.getBestScore());
            gamesPlayedLabel.setText("Games Played: " + currentUser.getGamesPlayed());
        }
    }



    @FXML
    private void handleLogout(ActionEvent event) {
        navigate(event, "fxml/login.fxml", controller -> {});
    }

    // Game functionality

    private void startNewGame() {
        questionReady = false;
        gameOver = false;

        artImages = DatabaseManager.getAllImages();
        System.out.println("[DEBUG] artImages size = " + (artImages == null ? "null" : artImages.size()));

        if (artImages == null || artImages.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No art images available.").showAndWait();
            return;
        }

        artImages.forEach(ai -> System.out.println("[DEBUG] " + ai.getPath() + " | " + ai.getCorrectAnswer()));
        Collections.shuffle(artImages);
        int cap = Math.min(artImages.size(), 10);
        artImages = new ArrayList<>(artImages.subList(0, cap));

        currentQuestionIndex = 0;
        score = 0;
        totalQuestions = artImages.size();

        showGamePane();
        loadNextQuestion();       // make the first question visible
    }

    private void loadNextQuestion() {
        questionReady = false;
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

        // Shuffle options per question
        List<String> options = new ArrayList<>(currentImage.getAllOptions());
        Collections.shuffle(options);

        for (int i = 0; i < optionButtons.length && i < options.size(); i++) {
            optionButtons[i].setText(options.get(i));
            optionButtons[i].setDisable(false);
            optionButtons[i].getStyleClass().removeAll("correct-answer", "wrong-answer", "selected-answer");
            optionButtons[i].getStyleClass().add("answer-button");
        }

        // Clear feedback and reset buttons
        feedbackLabel.setText("");
        nextQuestionButton.setDisable(true); // Disable next button like Location game
        submitAnswerButton.setDisable(true); // Disable submit button
        selectedAnswer = null;

        // Change button text depending on whether this is the last question
        if (currentQuestionIndex == totalQuestions - 1) {
            nextQuestionButton.setText("Finish 🎯");
        } else {
            nextQuestionButton.setText("Next");
        }

        questionReady = true;
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
        if (!questionReady || currentImage == null) return; // guard

        // Clear previous selection
        for (Button button : optionButtons) {
            button.getStyleClass().removeAll("selected-answer");
        }

        // Mark the selected answer
        selectedAnswer = selectedButton.getText();
        selectedButton.getStyleClass().add("selected-answer");
        
        // Enable submit button
        submitAnswerButton.setDisable(false);
    }

    @FXML
    private void handleSubmitAnswer() {
        if (selectedAnswer == null || currentImage == null) return;

        boolean isCorrect = currentImage.isCorrectAnswer(selectedAnswer);

        // Disable all buttons
        for (Button button : optionButtons) {
            button.setDisable(true);
        }
        submitAnswerButton.setDisable(true);

        // Style the buttons based on correctness
        if (isCorrect) {
            for (Button button : optionButtons) {
                if (button.getText().equals(selectedAnswer)) {
                    button.getStyleClass().removeAll("selected-answer");
                    button.getStyleClass().add("correct-answer");
                    break;
                }
            }
            feedbackLabel.setText("Correct! ✓ The artwork was created in " + currentImage.getCorrectAnswer());
            feedbackLabel.getStyleClass().removeAll("wrong-feedback");
            feedbackLabel.getStyleClass().add("correct-feedback");
            score++;
        } else {
            // Highlight the selected wrong answer
            for (Button button : optionButtons) {
                if (button.getText().equals(selectedAnswer)) {
                    button.getStyleClass().removeAll("selected-answer");
                    button.getStyleClass().add("wrong-answer");
                    break;
                }
            }

            // Highlight the correct answer
            for (Button button : optionButtons) {
                if (currentImage.isCorrectAnswer(button.getText())) {
                    button.getStyleClass().removeAll("answer-button");
                    button.getStyleClass().add("correct-answer");
                    break;
                }
            }

            feedbackLabel.setText("Wrong! The correct answer is " + currentImage.getCorrectAnswer() + 
                                ". The artwork was created in " + currentImage.getCorrectAnswer());
            feedbackLabel.getStyleClass().removeAll("correct-feedback");
            feedbackLabel.getStyleClass().add("wrong-feedback");
        }

        // Update score display
        scoreLabel.setText("Score: " + score);

        // Enable next button like Location game
        nextQuestionButton.setDisable(false);
    }

    @FXML
    private void handleNextQuestion() {
        if (gameOver) return;
        questionReady = false;
        currentQuestionIndex++;
        loadNextQuestion();

    }

    private void endGame() {
        gameOver = true;
        questionReady = false;
        for (Button b : optionButtons) b.setDisable(true);
        nextQuestionButton.setVisible(false);

        // Guarded DB writes
        if (currentUser != null) {
            DatabaseManager.updateUserScore(currentUser.getId(), score, totalQuestions);
            currentUser = DatabaseManager.refreshUser(currentUser.getId());
        } else {
            System.out.println("[WARN] endGame(): currentUser is null — skipping score update.");
        }

        // Show results UI
        showEndGamePane();

        // Calculate & show stats
        double percentage = totalQuestions == 0 ? 0 : (double) score / totalQuestions * 100.0;
        finalScoreLabel.setText("Final Score: " + score + " / " + totalQuestions);
        finalPercentageLabel.setText("Percentage: " + String.format("%.1f", percentage) + "%");

        // Best score banner (only if we have a user)
        if (currentUser != null && score >= currentUser.getHighScoreInt()) {
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
        navigate(event, "/fxml/login.fxml",controller -> {});
    }


    @FXML
    private void handleBackToMenu(ActionEvent event) {
        navigate(event, "/fxml/Menu.fxml", controller -> {});
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