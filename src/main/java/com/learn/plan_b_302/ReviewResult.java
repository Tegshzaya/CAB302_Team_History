package com.learn.plan_b_302;

/**
 * Represents the result of a single question in the game for review purposes
 */
public class ReviewResult {
    private final ArtImage artImage;
    private final String userAnswer;
    private final String correctAnswer;
    private final boolean isCorrect;
    private final int questionNumber;
    
    public ReviewResult(ArtImage artImage, String userAnswer, String correctAnswer, boolean isCorrect, int questionNumber) {
        this.artImage = artImage;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
        this.questionNumber = questionNumber;
    }
    
    public ArtImage getArtImage() {
        return artImage;
    }
    
    public String getUserAnswer() {
        return userAnswer;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public boolean isCorrect() {
        return isCorrect;
    }
    
    public int getQuestionNumber() {
        return questionNumber;
    }
    
    public String getImagePath() {
        return artImage.getPath();
    }
    
    public String getArtworkTitle() {
        return artImage.getTitle();
    }
    
    public String getHistoricalContext() {
        return artImage.getHistoricalContext();
    }
}