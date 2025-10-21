package com.learn.plan_b_302;

public class User {
    private int id;
    private String username;
    private int highScore;
    private int gamesPlayed;

    public User(int id, String username, int highScore) {
        this.id = id;
        this.username = username;
        this.highScore = highScore;
        this.gamesPlayed = 0;
    }

    public User(int id, String username, int highScore, int gamesPlayed) {
        this.id = id;
        this.username = username;
        this.highScore = highScore;
        this.gamesPlayed = gamesPlayed;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    // Setters
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", highScore=" + highScore +
                ", gamesPlayed=" + gamesPlayed +
                '}';
    }

    public String getBestScore() {
        return String.valueOf(highScore);
    }

    public int getHighScoreInt() {
        return highScore;
    }
    
    // Estimate location game best score (for display purposes)
    // This is a rough approximation - ideally you'd track location scores separately
    public int getLocationBestScore() {
        // Convert year game score (0-10) to location score estimate (0-30)
        // This assumes good year game performance correlates with good location game performance
        return Math.min(highScore * 3, 30);
    }
    
    // Get formatted location best score string
    public String getLocationBestScoreString() {
        return getLocationBestScore() + "/30";
    }
    
    // Get formatted year best score string  
    public String getYearBestScoreString() {
        return highScore + "/10";
    }
}
