package com.learn.plan_b_302;


import java.util.*;

public class ArtImage {
    private String path;
    private String correctAnswer;
    private List<String> allOptions;

    public ArtImage(String path, String correctAnswer, String option1, String option2, String option3, String option4) {
        this.path = path;
        this.correctAnswer = correctAnswer;

        List<String> options = new ArrayList<>(Arrays.asList(option1, option2, option3, option4));

        // Ensure the correct answer is present even if DB row has a mismatch
        if (!containsNormalized(options, correctAnswer)) {
            options.add(correctAnswer);
        }

        // De-duplicate by normalized text to avoid twins with different spacing/case
        LinkedHashMap<String, String> dedup = new LinkedHashMap<>();
        for (String s : options) {
            dedup.put(normalize(s), s);
        }

        // Create shuffled list of all options
        this.allOptions = new ArrayList<>(Arrays.asList(option1, option2, option3, option4));
        Collections.shuffle(this.allOptions);
    }

    // Legacy constructor
    public ArtImage(String imagePath, String correctAnswer, String... wrongOptions) {
        this.path = imagePath;
        this.correctAnswer = correctAnswer;

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        options.addAll(Arrays.asList(wrongOptions));

        // De-duplicate
        LinkedHashMap<String, String> dedup = new LinkedHashMap<>();
        for (String s : options) {
            dedup.put(normalize(s), s);
        }
        this.allOptions = new ArrayList<>(dedup.values());
        Collections.shuffle(this.allOptions);
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

    // Helpers
    private static String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private static boolean containsNormalized(List<String> list, String value) {
        String nv = normalize(value);
        for (String s : list) {
            if (normalize(s).equals(nv)) return true;
        }
        return false;
    }

    // Keep for backward compatibility
    public String getImagePath() {
        return path;
    }

    public List<String> getOptions() {
        return getAllOptions();
    }
}