package com.example.login_page;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class GuessLocationController {

    @FXML private ImageView photoView;
    @FXML private WebView mapView;
    @FXML private Label feedbackLabel;
    @FXML private Label roundLabel;
    @FXML private Label scoreLabel;
    @FXML private Label artworkInfo;
    @FXML private Button nextBtn;
    @FXML private Button submitBtn;

    private WebEngine engine;
    private JSObject window;

    private static final String CSV_PATH = "/com/example/login_page/data/guess_location.csv";
    private static final String IMG_DIR  = "/com/example/login_page/images/";
    private static final String MAP_HTML = "/com/example/login_page/web/map.html";

    private static class Round {
        final String filename; final double lat, lon; final String label;
        Round(String f, double la, double lo, String label) { this.filename=f; this.lat=la; this.lon=lo; this.label=label; }
    }

    private final List<Round> rounds = new ArrayList<>();
    private int index = -1, score = 0;
    private Double guessLat = null, guessLon = null;
    private boolean answered = false;

    @FXML
    public void initialize() {
        System.out.println("GuessLocationController: Initializing...");
        
        try { 
            loadDataset(); 
            System.out.println("GuessLocationController: Dataset loaded successfully");
        }
        catch (IOException e) { 
            System.err.println("GuessLocationController: Failed to load dataset: " + e.getMessage());
            fail("❌ Could not load dataset: " + e.getMessage()); 
            return; 
        }

        try {
            engine = mapView.getEngine();
            System.out.println("GuessLocationController: WebEngine created");
            
            engine.getLoadWorker().stateProperty().addListener((obs, o, s) -> {
                System.out.println("GuessLocationController: Map load state: " + s);
                if (s == Worker.State.SUCCEEDED) {
                    try {
                        window = (JSObject) engine.executeScript("window");
                        window.setMember("app", this); // enable window.app.onMapClick(...)
                        System.out.println("GuessLocationController: Map loaded and app member set");
                    } catch (Exception e) {
                        System.err.println("GuessLocationController: Error setting up map: " + e.getMessage());
                    }
                } else if (s == Worker.State.FAILED) {
                    System.err.println("GuessLocationController: Map failed to load");
                    fail("❌ Map failed to load. Please check your internet connection.");
                }
            });

            // load local HTML by URL so Leaflet CDN works fine
            String url = Objects.requireNonNull(getClass().getResource(MAP_HTML)).toExternalForm();
            System.out.println("GuessLocationController: Loading map from: " + url);
            engine.load(url);
        } catch (Exception e) {
            System.err.println("GuessLocationController: Error initializing map: " + e.getMessage());
            fail("❌ Error initializing map: " + e.getMessage());
            return;
        }

        nextRound();
    }

    private void loadDataset() throws IOException {
        try (InputStream in = Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH), "CSV not found: " + CSV_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line; int row = 0;
            while ((line = br.readLine()) != null) {
                row++; line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 4); // filename,lat,lon,label
                if (parts.length < 3) throw new IOException("Invalid row " + row + ": " + line);
                String f = parts[0].trim(); double la = Double.parseDouble(parts[1].trim());
                double lo = Double.parseDouble(parts[2].trim()); String label = (parts.length>=4?parts[3].trim():f);
                if (getClass().getResourceAsStream(IMG_DIR + f) == null) throw new IOException("Image not found: " + IMG_DIR + f);
                rounds.add(new Round(f, la, lo, label));
            }
        }
        if (rounds.isEmpty()) throw new IOException("CSV is empty.");
        Collections.shuffle(rounds, new Random());
    }

    private void nextRound() {
        index++;
        if (index >= rounds.size()) { 
            feedbackLabel.setText("🎉 Congratulations! You completed the game!\nFinal Score: " + score + " / " + (rounds.size() * 3) + " points");
            submitBtn.setDisable(true); 
            nextBtn.setDisable(true); 
            return; 
        }

        Round r = rounds.get(index);
        photoView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMG_DIR + r.filename))));
        roundLabel.setText("Round " + (index+1) + " / " + rounds.size());
        scoreLabel.setText("Score: " + score);
        
        // Update artwork info
        if (artworkInfo != null) {
            artworkInfo.setText("Can you guess where this famous artwork is located?");
        }
        
        guessLat = guessLon = null; 
        answered = false; 
        submitBtn.setDisable(false); 
        nextBtn.setDisable(true);
        feedbackLabel.setText("🌍 Click anywhere on the world map to place your guess, then click Submit Guess.");
    }

    /** Called from JS (map.html) when user clicks */
    public void onMapClick(double lat, double lon) {
        System.out.println("GuessLocationController: Map clicked at " + lat + ", " + lon);
        guessLat = lat; guessLon = lon;
        feedbackLabel.setText(String.format("📍 Pin placed at: %.4f°N, %.4f°E\nClick 'Submit Guess' to confirm your answer!", lat, lon));
    }

    @FXML
    private void onSubmit() {
        System.out.println("GuessLocationController: Submit button clicked");
        if (answered) return;
        if (guessLat == null || guessLon == null) { 
            System.out.println("GuessLocationController: No guess placed yet");
            feedbackLabel.setText("⚠️ Please click on the map first to place your guess!"); 
            return; 
        }
        
        System.out.println("GuessLocationController: Processing guess at " + guessLat + ", " + guessLon);

        Round r = rounds.get(index);
        double distKm = haversineKm(guessLat, guessLon, r.lat, r.lon);

        int pts; String msg; String emoji;
        if (distKm <= 10)       { pts=3; msg="🎯 Amazing! Perfect guess!"; emoji="🏆"; }
        else if (distKm <= 50)  { pts=2; msg="✅ Excellent! Very close!"; emoji="⭐"; }
        else if (distKm <= 200) { pts=1; msg="👍 Good try! Getting closer!"; emoji="👍"; }
        else                    { pts=0; msg="❌ Not quite right, but keep trying!"; emoji="💪"; }

        score += pts; 
        scoreLabel.setText("Score: " + score);
        
        String feedback = String.format("%s %s\n\n📍 Your guess: %.4f°N, %.4f°E\n🎯 Correct location: %s\n📏 Distance: %.1f km\n\n%s points earned! Click 'Next Round' to continue.",
                emoji, msg, guessLat, guessLon, r.label, distKm, pts);
        
        feedbackLabel.setText(feedback);

        try { 
            if (window != null) window.call("showResultLine", guessLat, guessLon, r.lat, r.lon); 
        } catch (Exception ignored) {}

        answered = true; 
        submitBtn.setDisable(true); 
        nextBtn.setDisable(false);
    }

    @FXML private void onNext() { if (answered) nextRound(); }

    @FXML
    private void onBackToMenu(ActionEvent event) {
        try {
            System.out.println("GuessLocationController: Back to menu button clicked");
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("SelectionScreen.fxml"),
                            "Missing FXML: SelectionScreen.fxml"));
            Parent root = loader.load();

            // Use the primary stage instead of creating new ones
            Stage stage = HelloApplication.getPrimaryStage();
            if (stage == null) {
                System.err.println("GuessLocationController: Primary stage is null, falling back to event source");
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
            
            System.out.println("GuessLocationController: Setting selection screen on stage");
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
            feedbackLabel.setText("❌ Error returning to menu. Please restart the application.");
        }
    }

    private void fail(String m){ feedbackLabel.setText(m); submitBtn.setDisable(true); nextBtn.setDisable(true); }

    private static double haversineKm(double lat1,double lon1,double lat2,double lon2){
        final double R=6371.0088;
        double dLat=Math.toRadians(lat2-lat1), dLon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return R*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
    }
}
