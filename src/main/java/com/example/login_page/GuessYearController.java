package com.example.login_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class GuessYearController {

    @FXML private ImageView photoView;
    @FXML private TextField yearField;
    @FXML private Label feedbackLabel;
    @FXML private Label roundLabel;
    @FXML private Label scoreLabel;
    @FXML private Label artworkInfo;
    @FXML private Button nextBtn;
    @FXML private Button skipBtn;

    private final List<GuessYearRound> rounds = new ArrayList<>();
    private GuessYearRound current;
    private int currentRoundIndex = 0;
    private int score = 0;

    private static final String CSV_PATH = "/com/example/login_page/data/guess_year.csv";
    private static final String IMG_DIR  = "/com/example/login_page/images/";

    @FXML
    public void initialize() {
        System.out.println("GuessYearController: Initializing...");
        try {
            loadDataset();
            System.out.println("GuessYearController: Dataset loaded successfully, " + rounds.size() + " rounds");
            // Start with first round
            currentRoundIndex = -1; // Will be incremented to 0 in nextRound()
            nextRound();
        } catch (IOException e) {
            System.err.println("GuessYearController: Failed to load dataset: " + e.getMessage());
            feedbackLabel.setText("❌ Could not load data: " + e.getMessage());
        }
    }

    private void loadDataset() throws IOException {
        System.out.println("GuessYearController: Loading dataset from: " + CSV_PATH);
        try (InputStream in = Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH),
                "CSV not found: " + CSV_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;
            int rowCount = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                if (parts.length != 2) throw new IOException("Invalid CSV row: " + line);
                String filename = parts[0].trim();
                int year = Integer.parseInt(parts[1].trim());
                
                String fullImagePath = IMG_DIR + filename;
                System.out.println("GuessYearController: Checking image: " + fullImagePath);
                if (getClass().getResourceAsStream(fullImagePath) == null) {
                    throw new IOException("Image not found: " + fullImagePath);
                }
                System.out.println("GuessYearController: Image found: " + filename + " -> " + year);
                rounds.add(new GuessYearRound(filename, year));
                rowCount++;
            }
            System.out.println("GuessYearController: Loaded " + rowCount + " rounds from CSV");
        }
        if (rounds.isEmpty()) throw new IOException("CSV empty.");
    }

    private void nextRound() {
        // Move to next round, cycling back to start if we reach the end
        currentRoundIndex = (currentRoundIndex + 1) % rounds.size();
        current = rounds.get(currentRoundIndex);

        System.out.println("Loading round " + (currentRoundIndex + 1) + ", year: " + current.getCorrectYear());

        // Update round and score labels
        roundLabel.setText((currentRoundIndex + 1) + " / " + rounds.size());
        scoreLabel.setText(String.valueOf(score));

        String fullPath = IMG_DIR + current.getFilename();
        System.out.println("Looking for image at: " + fullPath);
        InputStream img = getClass().getResourceAsStream(fullPath);
        if (img == null) {
            System.out.println("Image not found: " + fullPath);
            feedbackLabel.setText("❌ Image not found: " + current.getFilename());
            nextBtn.setDisable(true);
            return;
        }
        System.out.println("Image loaded successfully: " + fullPath);
        photoView.setImage(new Image(img));
        yearField.clear();
        yearField.setDisable(false);
        nextBtn.setDisable(true);            // lock Next until correct or skipped
        skipBtn.setDisable(false);           // enable skip button
        
        String artworkName = getArtworkName(current.getFilename());
        
        // Update artwork info
        if (artworkInfo != null) {
            artworkInfo.setText("Can you guess when " + artworkName + " was created?");
        }
        
        feedbackLabel.setText("🎨 Enter the year when you think this artwork was created, then click 'Submit Guess'.");
        feedbackLabel.getStyleClass().clear();
        feedbackLabel.getStyleClass().addAll("feedback-label", "info");
    }

    @FXML
    private void onSubmit() {
        System.out.println("GuessYearController: Submit button clicked");
        if (current == null) {
            System.out.println("GuessYearController: Current round is null!");
            feedbackLabel.setText("❌ Error: No current round loaded. Please restart the game.");
            return;
        }

        String text = yearField.getText();
        System.out.println("GuessYearController: User input: '" + text + "'");
        int guess;
        try {
            guess = Integer.parseInt(text.trim());
            System.out.println("GuessYearController: Parsed guess: " + guess);
        } catch (NumberFormatException e) {
            System.out.println("GuessYearController: Invalid number format: " + text);
            feedbackLabel.setText("❌ Please enter a valid year (e.g., 1801).");
            return;
        }

        System.out.println("GuessYearController: Comparing guess " + guess + " with correct answer " + current.getCorrectYear());
        if (guess == current.getCorrectYear()) {
            System.out.println("GuessYearController: Correct answer!");
            // Award points for correct answer
            score += 10;
            scoreLabel.setText(String.valueOf(score));
            
            // Show a friendly message
            String artworkName = getArtworkName(current.getFilename());
            feedbackLabel.setText("🎯 Perfect! " + artworkName + " was created in " + current.getCorrectYear() + 
                                ". You earned 10 points! Click 'Next Round' to continue.");
            feedbackLabel.getStyleClass().clear();
            feedbackLabel.getStyleClass().addAll("feedback-label", "success");

            yearField.setDisable(true);     // freeze input for this round
            nextBtn.setDisable(false);      // allow moving to next image
            skipBtn.setDisable(true);       // disable skip since they got it right
        } else {
            System.out.println("GuessYearController: Incorrect answer, difference: " + Math.abs(guess - current.getCorrectYear()));
            // Stay on the same image; allow trying again
            int diff = Math.abs(guess - current.getCorrectYear());
            feedbackLabel.getStyleClass().clear();
            feedbackLabel.getStyleClass().addAll("feedback-label", "warning");
            if (diff <= 5) {
                feedbackLabel.setText("❌ Close! You're within 5 years. Try again!");
            } else if (diff <= 20) {
                feedbackLabel.setText("❌ Getting warmer! You're within 20 years. Try again!");
            } else {
                feedbackLabel.setText("❌ Not quite. Try again!");
            }
            yearField.selectAll();          // convenience: select text for quick retype
            yearField.requestFocus();
        }
    }

    @FXML
    private void onNext() {
        System.out.println("Next button clicked! Current round: " + (currentRoundIndex + 1));
        nextRound();
    }

    @FXML
    private void onSkip() {
        System.out.println("Skip button clicked! Current round: " + (currentRoundIndex + 1));
        String artworkName = getArtworkName(current.getFilename());
        feedbackLabel.setText("⏭️ Skipped! The correct answer was " + current.getCorrectYear() + " for " + artworkName + 
                            ". No points earned. Click 'Next Round' to continue.");
        feedbackLabel.getStyleClass().clear();
        feedbackLabel.getStyleClass().addAll("feedback-label", "info");
        yearField.setDisable(true);
        nextBtn.setDisable(false);
        skipBtn.setDisable(true);
    }

    @FXML
    private void onBackToMenu(ActionEvent event) {
        try {
            System.out.println("GuessYearController: Back to menu button clicked");
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("SelectionScreen.fxml"),
                            "Missing FXML: SelectionScreen.fxml"));
            Parent root = loader.load();

            // Use the primary stage instead of creating new ones
            Stage stage = HelloApplication.getPrimaryStage();
            if (stage == null) {
                System.err.println("GuessYearController: Primary stage is null, falling back to event source");
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }
            
            Scene newScene = new Scene(root);
            
            // Apply the stylesheet to the new scene
            newScene.getStylesheets().add(getClass().getResource("/com/example/login_page/styles.css").toExternalForm());
            
            // Set fixed window size and disable resizing
            stage.setResizable(false);
            stage.setMinWidth(1200);
            stage.setMaxWidth(1200);
            stage.setMinHeight(900);
            stage.setMaxHeight(900);
            
            System.out.println("GuessYearController: Setting selection screen on stage");
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
            feedbackLabel.setText("❌ Error returning to menu. Please restart the application.");
        }
    }

    private String getArtworkName(String filename) {
        String name = filename.replace("images/", "").replace(".jpg", "");
        switch (name) {
            case "NapoleonCrossingAlps": return "Napoleon Crossing the Alps";
            case "MonaLisa": return "Mona Lisa";
            case "StarryNight": return "The Starry Night";
            case "TheScream": return "The Scream";
            case "GirlWithPearlEarring": return "Girl with a Pearl Earring";
            case "AmericanGothic": return "American Gothic";
            default: return "this artwork";
        }
    }
}
