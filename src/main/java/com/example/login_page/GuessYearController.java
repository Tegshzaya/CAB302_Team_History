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

    private final List<GuessYearRound> rounds = new ArrayList<>();
    private GuessYearRound current;

    private static final String CSV_PATH = "/com/example/login_page/data/guess_year.csv";
    private static final String IMG_DIR  = "/com/example/login_page/images/";

    @FXML
    public void initialize() {
        try {
            loadDataset();
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
        Collections.shuffle(rounds);
        current = rounds.get(0);

        InputStream img = getClass().getResourceAsStream(IMG_DIR + current.getFilename());
        if (img == null) {
            feedbackLabel.setText("❌ Image not found: " + current.getFilename());
            nextBtn.setDisable(true);
            return;
        }
        photoView.setImage(new Image(img));
        yearField.clear();
        yearField.setDisable(false);
        nextBtn.setDisable(true);            // lock Next until correct
        feedbackLabel.setText("Guess the year of this image!");
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
            feedbackLabel.setText("✅ Correct! Year was " + current.getCorrectYear() + ". Click Next for another image.");
            // Optional modal (uncomment if you want a popup)
            // Alert ok = new Alert(Alert.AlertType.INFORMATION, "Correct! Click Next for another image.");
            // ok.setHeaderText(null);
            // ok.showAndWait();

            yearField.setDisable(true);     // freeze input for this round
            nextBtn.setDisable(false);      // allow moving to next image
        } else {
            // Stay on the same image; allow trying again
            feedbackLabel.setText("❌ Not quite. Try again!");
            yearField.selectAll();          // convenience: select text for quick retype
            yearField.requestFocus();
        }
    }

    @FXML
    private void onNext() {
        nextRound();
    }
}
