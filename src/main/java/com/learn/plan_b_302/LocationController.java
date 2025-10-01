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

public class LocationController extends BaseController {

    @FXML private ImageView photoView;
    @FXML private WebView mapView;
    @FXML private Label feedbackLabel;
    @FXML private Label roundLabel;
    @FXML private Label scoreLabel;
    @FXML private Button nextBtn;
    @FXML private Button submitBtn;

    private WebEngine engine;
    private JSObject window;

    private static final String CSV_PATH = "/data/guess_location.csv";
    private static final String IMG_DIR  = "/images/";
    private static final String MAP_HTML = "/web/map.html";

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
        try { loadDataset(); }
        catch (IOException e) { fail("❌ Could not load dataset: " + e.getMessage()); return; }

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
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
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
        Collections.shuffle(rounds, new Random());
    }

    private void nextRound() {
        index++;
        if (index >= rounds.size()) {
            feedbackLabel.setText("✅ Finished! Final score: " + score + " / " + rounds.size());
            submitBtn.setDisable(true); nextBtn.setDisable(true);
            return;
        }
        Round r = rounds.get(index);
        photoView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMG_DIR + r.filename))));
        roundLabel.setText("Round " + (index+1) + " / " + rounds.size());
        scoreLabel.setText("Score: " + score);

        guessLat = guessLon = null; answered = false;
        submitBtn.setDisable(false); nextBtn.setDisable(true);
        feedbackLabel.setText("Drop a pin on the map, then click Submit Guess.");
    }

    /** Called from JS when user clicks on the map */
    public void onMapClick(double lat, double lon) {
        guessLat = lat; guessLon = lon;
        feedbackLabel.setText(String.format("Selected: %.4f, %.4f — click Submit Guess.", lat, lon));
    }

    @FXML
    private void onSubmit() {
        if (answered) return;
        if (guessLat == null || guessLon == null) { feedbackLabel.setText("Please click on the map first."); return; }

        Round r = rounds.get(index);
        double distKm = haversineKm(guessLat, guessLon, r.lat, r.lon);

        int pts; String msg;
        if (distKm <= 10)       { pts=3; msg="🎯 Amazing!"; }
        else if (distKm <= 50)  { pts=2; msg="✅ Good job!"; }
        else if (distKm <= 200) { pts=1; msg="👍 Close!"; }
        else                    { pts=0; msg="❌ Not close."; }

        score += pts; scoreLabel.setText("Score: " + score);
        feedbackLabel.setText(String.format("%s You were %.1f km away. Correct: %s (%.5f, %.5f). Click Next.",
                msg, distKm, r.label, r.lat, r.lon));

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

    private void fail(String m){ feedbackLabel.setText(m); submitBtn.setDisable(true); nextBtn.setDisable(true); }
}
