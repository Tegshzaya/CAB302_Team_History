package com.learn.plan_b_302;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;

public class ArtImage {
    private String path;
    private String correctAnswer;
    private List<String> allOptions;

    public ArtImage(String path, String correctAnswer, String option1, String option2, String option3, String option4) {
        this.path = path;
        this.correctAnswer = correctAnswer;

        // Create shuffled list of all options
        this.allOptions = new ArrayList<>(Arrays.asList(option1, option2, option3, option4));
        Collections.shuffle(this.allOptions);
    }

    // Legacy constructor for backward compatibility
    public ArtImage(String imagePath, String correctAnswer, String... wrongOptions) {
        this.path = imagePath;
        this.correctAnswer = correctAnswer;

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        options.addAll(Arrays.asList(wrongOptions));
        Collections.shuffle(options);
        this.allOptions = options;
    }

    public boolean isCorrectAnswer(String answer) {
        return correctAnswer.equals(answer);
    }

    public List<String> getAllOptions() {
        return new ArrayList<>(allOptions);
    }

    // Getters
    public String getPath() {
        return path;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // Keep for backward compatibility
    public String getImagePath() {
        return path;
    }

    public List<String> getOptions() {
        return getAllOptions();
    }
}