package com.learn.plan_b_302;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple record of a user's game session - just the basic information
 */
public class UserGameRecord {
    private int id;
    private int userId;
    private String gameType; // "YEAR_GAME" or "LOCATION_GAME" 
    private int score;
    private LocalDateTime datePlayed;
    
    // Constructor for creating new records (without ID)
    public UserGameRecord(int userId, String gameType, int score, LocalDateTime datePlayed) {
        this.userId = userId;
        this.gameType = gameType;
        this.score = score;
        this.datePlayed = datePlayed;
    }
    
    // Constructor for loading from database (with ID)
    public UserGameRecord(int id, int userId, String gameType, int score, LocalDateTime datePlayed) {
        this.id = id;
        this.userId = userId;
        this.gameType = gameType;
        this.score = score;
        this.datePlayed = datePlayed;
    }
    
    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getGameType() { return gameType; }
    public int getScore() { return score; }
    public LocalDateTime getDatePlayed() { return datePlayed; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setGameType(String gameType) { this.gameType = gameType; }
    public void setScore(int score) { this.score = score; }
    public void setDatePlayed(LocalDateTime datePlayed) { this.datePlayed = datePlayed; }
    
    // Utility methods
    public String getGameTypeDisplay() {
        return switch (gameType) {
            case "YEAR_GAME" -> "📅 Year Game";
            case "LOCATION_GAME" -> "🌍 Location Game";
            default -> gameType;
        };
    }
    
    public String getFormattedDate() {
        return datePlayed.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
    }
    
    public String getScoreDisplay() {
        return switch (gameType) {
            case "YEAR_GAME" -> score + "/10";
            case "LOCATION_GAME" -> score + "/30";
            default -> String.valueOf(score);
        };
    }
    
    @Override
    public String toString() {
        return "UserGameRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", gameType='" + gameType + '\'' +
                ", score=" + score +
                ", datePlayed=" + datePlayed +
                '}';
    }
}