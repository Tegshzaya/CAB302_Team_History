package com.example.login_page;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.*;

public class GuessYearController {

    @FXML private ImageView photoView;
    @FXML private TextField yearField;
    @FXML private Label feedbackLabel;
    @FXML private Button nextBtn;   // NEW: wired to FXML
    @FXML private Button skipBtn;   // NEW: skip button

    private final List<GuessYearRound> rounds = new ArrayList<>();
    private GuessYearRound current;
    private int currentRoundIndex = 0;

    private static final String CSV_PATH = "/com/example/login_page/data/guess_year.csv";
    private static final String IMG_DIR  = "/com/example/login_page/";

    @FXML
    public void initialize() {
        try {
            loadDataset();
            // Start with first round
            currentRoundIndex = -1; // Will be incremented to 0 in nextRound()
            nextRound();
        } catch (IOException e) {
            feedbackLabel.setText("❌ Could not load data: " + e.getMessage());
        }
    }

    private void loadDataset() throws IOException {
        try (InputStream in = Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH),
                "CSV not found: " + CSV_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                if (parts.length != 2) throw new IOException("Invalid CSV row: " + line);
                String filename = parts[0].trim();
                int year = Integer.parseInt(parts[1].trim());
                if (getClass().getResourceAsStream(IMG_DIR + filename) == null) {
                    throw new IOException("Image not found: " + IMG_DIR + filename);
                }
                rounds.add(new GuessYearRound(filename, year));
            }
        }
        if (rounds.isEmpty()) throw new IOException("CSV empty.");
    }

    private void nextRound() {
        // Move to next round, cycling back to start if we reach the end
        currentRoundIndex = (currentRoundIndex + 1) % rounds.size();
        current = rounds.get(currentRoundIndex);

        System.out.println("Loading round " + (currentRoundIndex + 1) + ", year: " + current.getCorrectYear());

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
        feedbackLabel.setText("Guess the year of " + artworkName + "! (Round " + (currentRoundIndex + 1) + ")");
        feedbackLabel.getStyleClass().clear();
        feedbackLabel.getStyleClass().addAll("feedback-label", "info");
    }

    @FXML
    private void onSubmit() {
        if (current == null) return;

        String text = yearField.getText();
        int guess;
        try {
            guess = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid year.");
            return;
        }

        if (guess == current.getCorrectYear()) {
            // Show a friendly message (non-blocking label + optional alert)
            String artworkName = getArtworkName(current.getFilename());
            feedbackLabel.setText("✅ Correct! " + artworkName + " was created in " + current.getCorrectYear() + ". Click Next for another artwork!");
            feedbackLabel.getStyleClass().clear();
            feedbackLabel.getStyleClass().addAll("feedback-label", "success");
            // Optional modal (uncomment if you want a popup)
            // Alert ok = new Alert(Alert.AlertType.INFORMATION, "Correct! Click Next for another image.");
            // ok.setHeaderText(null);
            // ok.showAndWait();

            yearField.setDisable(true);     // freeze input for this round
            nextBtn.setDisable(false);      // allow moving to next image
            skipBtn.setDisable(true);       // disable skip since they got it right
        } else {
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
        feedbackLabel.setText("⏭️ Skipped! The correct answer was " + current.getCorrectYear() + " for " + artworkName + ". Click Next for another artwork!");
        feedbackLabel.getStyleClass().clear();
        feedbackLabel.getStyleClass().addAll("feedback-label", "info");
        yearField.setDisable(true);
        nextBtn.setDisable(false);
        skipBtn.setDisable(true);
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
