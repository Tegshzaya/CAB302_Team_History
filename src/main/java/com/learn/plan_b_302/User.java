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
}