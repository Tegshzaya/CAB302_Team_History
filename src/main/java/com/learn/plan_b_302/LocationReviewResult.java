package com.learn.plan_b_302;

/**
 * Represents the result of a single location guess in the game for review purposes
 */
public class LocationReviewResult {
    private final String imagePath;
    private final String imageTitle;
    private final String locationName;
    private final double correctLat;
    private final double correctLon;
    private final Double guessLat;
    private final Double guessLon;
    private final double distanceKm;
    private final int pointsEarned;
    private final int roundNumber;
    private final String locationInsight;
    
    public LocationReviewResult(String imagePath, String imageTitle, String locationName, 
                              double correctLat, double correctLon, 
                              Double guessLat, Double guessLon, 
                              double distanceKm, int pointsEarned, int roundNumber,
                              String locationInsight) {
        this.imagePath = imagePath;
        this.imageTitle = imageTitle;
        this.locationName = locationName;
        this.correctLat = correctLat;
        this.correctLon = correctLon;
        this.guessLat = guessLat;
        this.guessLon = guessLon;
        this.distanceKm = distanceKm;
        this.pointsEarned = pointsEarned;
        this.roundNumber = roundNumber;
        this.locationInsight = locationInsight;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public String getImageTitle() {
        return imageTitle;
    }
    
    public String getLocationName() {
        return locationName;
    }
    
    public double getCorrectLat() {
        return correctLat;
    }
    
    public double getCorrectLon() {
        return correctLon;
    }
    
    public Double getGuessLat() {
        return guessLat;
    }
    
    public Double getGuessLon() {
        return guessLon;
    }
    
    public double getDistanceKm() {
        return distanceKm;
    }
    
    public int getPointsEarned() {
        return pointsEarned;
    }
    
    public int getRoundNumber() {
        return roundNumber;
    }
    
    public String getLocationInsight() {
        return locationInsight;
    }
    
    public boolean isCorrect() {
        return pointsEarned > 0;
    }
    
    public String getAccuracyDescription() {
        if (distanceKm <= 10) return "🎯 Amazing! (≤10km)";
        else if (distanceKm <= 50) return "✅ Good job! (≤50km)";
        else if (distanceKm <= 200) return "👍 Close! (≤200km)";
        else return "❌ Not close (>200km)";
    }
    
    public boolean hasGuess() {
        return guessLat != null && guessLon != null;
    }
}