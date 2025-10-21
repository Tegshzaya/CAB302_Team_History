package com.learn.plan_b_302;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single game session in the player's history
 */
public class GameHistory {
    private int id;
    private int userId;
    private String gameType; // "YEAR_GAME" or "LOCATION_GAME"
    private int score;
    private int maxPossibleScore;
    private int questionsAnswered;
    private int correctAnswers;
    private LocalDateTime playedAt;
    private String gameDetails; // JSON string with detailed results
    
    // Constructor for creating new game history (without ID)
    public GameHistory(int userId, String gameType, int score, int maxPossibleScore, 
                      int questionsAnswered, int correctAnswers, LocalDateTime playedAt, 
                      String gameDetails) {
        this.userId = userId;
        this.gameType = gameType;
        this.score = score;
        this.maxPossibleScore = maxPossibleScore;
        this.questionsAnswered = questionsAnswered;
        this.correctAnswers = correctAnswers;
        this.playedAt = playedAt;
        this.gameDetails = gameDetails;
    }
    
    // Constructor for loading from database (with ID)
    public GameHistory(int id, int userId, String gameType, int score, int maxPossibleScore,
                      int questionsAnswered, int correctAnswers, LocalDateTime playedAt,
                      String gameDetails) {
        this.id = id;
        this.userId = userId;
        this.gameType = gameType;
        this.score = score;
        this.maxPossibleScore = maxPossibleScore;
        this.questionsAnswered = questionsAnswered;
        this.correctAnswers = correctAnswers;
        this.playedAt = playedAt;
        this.gameDetails = gameDetails;
    }
    
    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getGameType() { return gameType; }
    public int getScore() { return score; }
    public int getMaxPossibleScore() { return maxPossibleScore; }
    public int getQuestionsAnswered() { return questionsAnswered; }
    public int getCorrectAnswers() { return correctAnswers; }
    public LocalDateTime getPlayedAt() { return playedAt; }
    public String getGameDetails() { return gameDetails; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setGameType(String gameType) { this.gameType = gameType; }
    public void setScore(int score) { this.score = score; }
    public void setMaxPossibleScore(int maxPossibleScore) { this.maxPossibleScore = maxPossibleScore; }
    public void setQuestionsAnswered(int questionsAnswered) { this.questionsAnswered = questionsAnswered; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }
    public void setGameDetails(String gameDetails) { this.gameDetails = gameDetails; }
    
    // Utility methods
    public double getAccuracyPercentage() {
        return questionsAnswered == 0 ? 0.0 : (double) correctAnswers / questionsAnswered * 100.0;
    }
    
    public double getScorePercentage() {
        return maxPossibleScore == 0 ? 0.0 : (double) score / maxPossibleScore * 100.0;
    }
    
    public String getFormattedScore() {
        return score + "/" + maxPossibleScore;
    }
    
    public String getFormattedAccuracy() {
        return correctAnswers + "/" + questionsAnswered + " (" + String.format("%.1f%%", getAccuracyPercentage()) + ")";
    }
    
    public String getFormattedDate() {
        return playedAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }
    
    public String getFormattedDateShort() {
        return playedAt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
    
    public String getGameTypeDisplay() {
        return switch (gameType) {
            case "YEAR_GAME" -> "📅 Year Game";
            case "LOCATION_GAME" -> "🌍 Location Game";
            default -> gameType;
        };
    }
    
    public String getPerformanceLevel() {
        double percentage = getScorePercentage();
        if (percentage >= 90) return "🏆 Excellent";
        else if (percentage >= 75) return "⭐ Great";
        else if (percentage >= 60) return "👍 Good";
        else if (percentage >= 40) return "📚 Learning";
        else return "💪 Keep Trying";
    }
    
    @Override
    public String toString() {
        return "GameHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", gameType='" + gameType + '\'' +
                ", score=" + score +
                ", maxPossibleScore=" + maxPossibleScore +
                ", questionsAnswered=" + questionsAnswered +
                ", correctAnswers=" + correctAnswers +
                ", playedAt=" + playedAt +
                '}';
    }
}