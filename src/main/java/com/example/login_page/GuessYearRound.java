package com.example.login_page;

public class GuessYearRound {
    private final String filename; // e.g., "moon_landing.jpg"
    private final int correctYear;

    public GuessYearRound(String filename, int correctYear) {
        this.filename = filename;
        this.correctYear = correctYear;
    }

    public String getFilename() { return filename; }
    public int getCorrectYear() { return correctYear; }
}
