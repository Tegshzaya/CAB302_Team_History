package com.learn.plan_b_302;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LocationController extends BaseController {

    @FXML private ImageView photoView;
    @FXML private WebView mapView;
    @FXML private Label feedbackLabel;
    @FXML private Label roundLabel;
    @FXML private Label scoreLabel;
    @FXML private Button nextBtn;
    @FXML private Button submitBtn;
    
    // End game pane controls
    @FXML private SplitPane gamePane;
    @FXML private VBox endGamePane;
    @FXML private Label finalScoreLabel;
    @FXML private Label finalAccuracyLabel;
    @FXML private Button checkAndLearnButton;
    @FXML private Button playAgainButton;

    private WebEngine engine;
    private JSObject window;

    private static final String CSV_PATH = "/data/guess_location.csv";
    private static final String IMG_DIR  = "/images/";
    private static final String MAP_HTML = "/web/map.html";

    private static class Round {
        final String filename; final double lat, lon; final String label;
        final String insight;
        
        Round(String f, double la, double lo, String label) { 
            this.filename=f; 
            this.lat=la; 
            this.lon=lo; 
            this.label=label;
            this.insight = generateLocationInsight(f, label);
        }
        
        private static String generateLocationInsight(String filename, String locationName) {
            // Generate location-specific insights
            if (locationName.toLowerCase().contains("louvre") || locationName.toLowerCase().contains("paris")) {
                return "The Louvre Museum in Paris is the world's largest art museum and a historic monument. Originally built as a fortress in the 12th century, it became a royal palace before transforming into a public museum during the French Revolution in 1793.";
            } else if (locationName.toLowerCase().contains("chicago")) {
                return "Chicago, known as the 'Windy City', is famous for its stunning architecture, deep-dish pizza, and rich cultural heritage. The Art Institute of Chicago houses one of the world's finest collections of Impressionist and Post-Impressionist art.";
            } else if (locationName.toLowerCase().contains("london")) {
                return "London, the capital of England and the United Kingdom, is a global hub for culture, history, and the arts. Its museums, galleries, and historic landmarks attract millions of visitors from around the world each year.";
            } else if (locationName.toLowerCase().contains("hague")) {
                return "The Hague is the seat of the Dutch government and home to the International Court of Justice. The Mauritshuis museum houses the world's finest collection of Dutch Golden Age paintings, including works by Vermeer and Rembrandt.";
            } else if (locationName.toLowerCase().contains("oslo")) {
                return "Oslo, the capital of Norway, is known for its green spaces, maritime heritage, and vibrant cultural scene. The city's museums showcase everything from Viking history to modern art and Norwegian cultural traditions.";
            } else if (locationName.toLowerCase().contains("new york") || locationName.toLowerCase().contains("moma")) {
                return "New York City is a global center for art, culture, and commerce. The Museum of Modern Art (MoMA) houses the world's most influential collection of modern and contemporary art, from painting to electronic media.";
            } else if (locationName.toLowerCase().contains("borodino")) {
                return "The Battle of Borodino (1812) was a major engagement during Napoleon's invasion of Russia. Located near Moscow, this site now houses a museum commemorating one of the largest and bloodiest single-day battles of the Napoleonic Wars.";
            } else if (locationName.toLowerCase().contains("waterloo")) {
                return "The Battle of Waterloo (1815) marked Napoleon's final defeat and the end of the Napoleonic Wars. This Belgian site is now a UNESCO World Heritage location with museums and monuments preserving this pivotal moment in European history.";
            } else if (locationName.toLowerCase().contains("washington") || locationName.toLowerCase().contains("capitol")) {
                return "Washington, D.C., the capital of the United States, is home to numerous monuments, museums, and government buildings. The U.S. Capitol building houses important historical documents and artworks that tell the story of American democracy.";
            } else {
                return "This location represents an important site in world history and culture. Each place on our planet has unique stories, traditions, and contributions that have shaped human civilization throughout the ages.";
            }
        }
    }

    private final List<Round> rounds = new ArrayList<>();
    private List<Round> gameRounds = new ArrayList<>(); // Limited to 10 rounds for the game
    private int index = -1, score = 0;
    private Double guessLat = null, guessLon = null;
    private boolean answered = false;
    private List<LocationReviewResult> gameResults = new ArrayList<>();
    
    private User currentUser;  // store the logged-in user
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("LocationController: User set to " + (user != null ? user.getUsername() : "null"));
    }

    @FXML
    public void initialize() {
        System.out.println("LocationController: Initializing...");
        
        try { loadDataset(); }
        catch (IOException e) { 
            System.err.println("LocationController: Failed to load dataset - " + e.getMessage());
            fail("❌ Could not load dataset: " + e.getMessage()); 
            return; 
        }
        
        System.out.println("LocationController: Dataset loaded with " + rounds.size() + " rounds");
        engine = mapView.getEngine();

        // Optional: see JS console logs in your IDE (handy to debug)
        engine.setOnAlert(evt -> System.out.println("[WebView ALERT] " + evt.getData()));

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                window = (JSObject) engine.executeScript("window");
                window.setMember("app", this);

                // Forward console.log/error to Java output
                engine.executeScript("""
                  (function(){
                    var L=console.log, E=console.error;
                    console.log=function(){ try{app.javaLog('LOG', Array.prototype.join.call(arguments,' '));}catch(_){} L.apply(console, arguments); };
                    console.error=function(){ try{app.javaLog('ERR', Array.prototype.join.call(arguments,' '));}catch(_){} E.apply(console, arguments); };
                  })();
                """);

                // Build the map only AFTER WebView is laid out
                Platform.runLater(() -> {
                    engine.executeScript("if (window.initMap) window.initMap();");

                    // Schedule a few redraw nudges; WebKit sometimes needs multiple passes
                    nudgeRedraw(120);
                    nudgeRedraw(400);
                    nudgeRedraw(900);
                });

                // Keep it fresh on future resizes
                mapView.widthProperty().addListener((o, ov, nv) -> refreshLeafletSize());
                mapView.heightProperty().addListener((o, ov, nv) -> refreshLeafletSize());
            }
        });

        // Load local HTML via URL (keeps proper base for relative paths)
        String url = Objects.requireNonNull(getClass().getResource(MAP_HTML)).toExternalForm();
        engine.load(url);

        nextRound();
    }

    /** One-shot delayed redraw helper for smoothing the work of the map */
    private void nudgeRedraw(long delayMs) {
        new Thread(() -> {
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
            Platform.runLater(this::refreshLeafletSize);
        }).start();
    }

    /** Force Leaflet to redraw tiles completely (fixes grey patches) */
    private void refreshLeafletSize() {
        try {
            engine.executeScript("""
                if (window.forceRedraw) window.forceRedraw();
            """);
        } catch (Exception ignored) {}
    }

    private void loadDataset() throws IOException {
        // To robust the game used try method
        try (InputStream in = Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH), "CSV not found: " + CSV_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line; int row = 0;
            while ((line = br.readLine()) != null) {
                row++; line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 4); // filename,lat,lon,label
                if (parts.length < 3) throw new IOException("Invalid row " + row + ": " + line);
                String f = parts[0].trim();
                double la = Double.parseDouble(parts[1].trim());
                double lo = Double.parseDouble(parts[2].trim());
                String label = (parts.length >= 4) ? parts[3].trim() : f;

                if (getClass().getResourceAsStream(IMG_DIR + f) == null)
                    throw new IOException("Image not found: " + IMG_DIR + f);

                rounds.add(new Round(f, la, lo, label));
            }
        }
        if (rounds.isEmpty()) throw new IOException("CSV is empty.");
        
        // Select 10 random rounds for the game
        Collections.shuffle(rounds, new Random());
        int maxRounds = Math.min(10, rounds.size());
        gameRounds = new ArrayList<>(rounds.subList(0, maxRounds));
        gameResults.clear();
    }

    private void nextRound() {
        index++;
        if (index >= gameRounds.size()) {
            endGame();
            return;
        }
        Round r = gameRounds.get(index);
        photoView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMG_DIR + r.filename))));
        roundLabel.setText("Round " + (index+1) + " / " + gameRounds.size());
        scoreLabel.setText("Score: " + score);

        guessLat = guessLon = null; answered = false;
        submitBtn.setDisable(false); nextBtn.setDisable(true);
        feedbackLabel.setText("Drop a pin on the map, then click Submit Guess.");
    }

    /** Called from JS for console forwarding */
    public void javaLog(String level, String message) {
        System.out.println("[WebView " + level + "] " + message);
    }
    
    /** Called from JS when user clicks on the map */
    public void onMapClick(double lat, double lon) {
        guessLat = lat; guessLon = lon;
        System.out.println("LocationController: Map clicked at " + lat + ", " + lon);
        feedbackLabel.setText(String.format("Selected: %.4f, %.4f — click Submit Guess.", lat, lon));
    }

    @FXML
    private void onSubmit() {
        if (answered) return;
        if (guessLat == null || guessLon == null) { feedbackLabel.setText("Please click on the map first."); return; }

        Round r = gameRounds.get(index);
        double distKm = haversineKm(guessLat, guessLon, r.lat, r.lon);

        int pts; String msg;
        if (distKm <= 10)       { pts=3; msg="🎯 Amazing!"; }
        else if (distKm <= 50)  { pts=2; msg="✅ Good job!"; }
        else if (distKm <= 200) { pts=1; msg="👍 Close!"; }
        else                    { pts=0; msg="❌ Not close."; }

        score += pts; scoreLabel.setText("Score: " + score);
        feedbackLabel.setText(String.format("%s You were %.1f km away. Correct: %s (%.5f, %.5f). Click Next.",
                msg, distKm, r.label, r.lat, r.lon));

        // Store result for review
        String imageTitle = extractImageTitle(r.filename);
        LocationReviewResult result = new LocationReviewResult(
            IMG_DIR + r.filename, imageTitle, r.label, 
            r.lat, r.lon, guessLat, guessLon, 
            distKm, pts, index + 1, r.insight
        );
        gameResults.add(result);

        try { if (window != null) window.call("showResultLine", guessLat, guessLon, r.lat, r.lon); } catch (Exception ignored) {}

        answered = true; submitBtn.setDisable(true); nextBtn.setDisable(false);
        System.out.printf("distKm=%.3f (expect ≈1.0 km for Louvre test)%n", distKm);
    }

    @FXML private void onNext() { if (answered) nextRound(); }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0088; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    }


    @FXML
    private void onSelectionScreen(ActionEvent event) throws IOException{
        navigate(event, "/fxml/Menu.fxml",controller -> {});
    }

    @FXML
    private void onBackToMenu(ActionEvent event) throws IOException{
        navigate(event, "/fxml/Menu.fxml",controller -> {});
    }

    private void endGame() {
        // Calculate final statistics
        int totalLocations = gameResults.size();
        double totalDistance = 0;
        int locationsWithGuesses = 0;
        
        for (LocationReviewResult result : gameResults) {
            if (result.hasGuess()) {
                locationsWithGuesses++;
                totalDistance += result.getDistanceKm();
            }
        }
        
        double averageDistance = locationsWithGuesses > 0 ? totalDistance / locationsWithGuesses : 0;
        
        // Update final score displays
        finalScoreLabel.setText("Final Score: " + score + " / 30");
        finalAccuracyLabel.setText(String.format("Average Distance: %.1f km", averageDistance));
        
        // Show end game pane
        showEndGamePane();
    }
    
    private void showGamePane() {
        gamePane.setVisible(true);
        endGamePane.setVisible(false);
    }
    
    private void showEndGamePane() {
        gamePane.setVisible(false);
        endGamePane.setVisible(true);
    }
    
    @FXML
    private void handleCheckAndLearn(ActionEvent event) {
        navigate(event, "/fxml/location-review.fxml", controller -> {
            if (controller instanceof LocationReviewController lrc) {
                lrc.setReviewData(new ArrayList<>(gameResults), currentUser);
            }
        });
    }
    
    @FXML
    private void handlePlayAgain() {
        // Reset game state
        index = -1;
        score = 0;
        gameResults.clear();
        
        // Reload dataset and select new random rounds
        try {
            loadDataset();
        } catch (IOException e) {
            System.err.println("Failed to reload dataset: " + e.getMessage());
            return;
        }
        
        // Show game pane and start first round
        showGamePane();
        nextRound();
    }
    
    private String extractImageTitle(String filename) {
        if (filename == null) return "Unknown Location";
        
        // Remove file extension and convert underscores to spaces
        String title = filename;
        if (title.contains(".")) {
            title = title.substring(0, title.lastIndexOf("."));
        }
        title = title.replace("_", " ").replace("-", " ");
        
        // Capitalize each word
        String[] words = title.split(" ");
        StringBuilder formatted = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) formatted.append(" ");
            String word = words[i].toLowerCase();
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    formatted.append(word.substring(1));
                }
            }
        }
        
        return formatted.toString();
    }
    
    private void fail(String m){ feedbackLabel.setText(m); submitBtn.setDisable(true); nextBtn.setDisable(true); }
}
