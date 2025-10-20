package com.learn.plan_b_302;


import java.util.*;

public class ArtImage {
    private String path;
    private String correctAnswer;
    private List<String> allOptions;
    private String title;
    private String historicalContext;

    public ArtImage(String path, String correctAnswer, String option1, String option2, String option3, String option4) {
        this.path = path;
        this.correctAnswer = correctAnswer;
        this.title = extractTitleFromPath(path);
        this.historicalContext = generateHistoricalContext(path, correctAnswer);

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
        this.title = extractTitleFromPath(imagePath);
        this.historicalContext = generateHistoricalContext(imagePath, correctAnswer);

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
    
    public String getTitle() {
        return title;
    }
    
    public String getHistoricalContext() {
        return historicalContext;
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
    
    // Extract artwork title from file path
    private static String extractTitleFromPath(String path) {
        if (path == null || !path.contains("/")) return "Unknown Artwork";
        
        String filename = path.substring(path.lastIndexOf("/") + 1);
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        
        // Convert underscores to spaces and clean up
        String title = filename.replace("_", " ").replace("-", " ");
        
        // Handle specific artwork names
        switch (filename.toLowerCase()) {
            case "mona_lisa": return "Mona Lisa";
            case "starry_night": return "The Starry Night";
            case "scream": return "The Scream";
            case "girl_pearl": return "Girl with a Pearl Earring";
            case "american_gothic": return "American Gothic";
            case "battle_of_borodino": return "Battle of Borodino";
            case "battle_of_trafalgar": return "Battle of Trafalgar";
            case "battleofwaterloo": return "Battle of Waterloo";
            case "death_of_marat_by_david": return "The Death of Marat";
            case "declaration_of_independence": return "Declaration of Independence";
            case "eugene_ferdinand": return "Liberty Leading the People";
            default: return formatTitle(title);
        }
    }
    
    private static String formatTitle(String title) {
        if (title == null || title.isEmpty()) return "Unknown Artwork";
        
        String[] words = title.split(" ");
        StringBuilder formatted = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) formatted.append(" ");
            String word = words[i].toLowerCase();
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    formatted.append(word.substring(1));
                }
            }
        }
        
        return formatted.toString();
    }
    
    // Generate historical context based on artwork and year
    private static String generateHistoricalContext(String path, String year) {
        if (path == null) return "Historical context not available.";
        
        String filename = path.substring(path.lastIndexOf("/") + 1).toLowerCase();
        int yearInt = parseYear(year);
        
        // Specific historical contexts for known artworks
        if (filename.contains("mona_lisa")) {
            return "Created during the Italian Renaissance (" + year + ") by Leonardo da Vinci. This portrait represents the pinnacle of Renaissance art and is famous for the subject's enigmatic smile and da Vinci's innovative sfumato technique.";
        } else if (filename.contains("starry_night")) {
            return "Painted by Vincent van Gogh in " + year + " during his stay at the Saint-Paul-de-Mausole asylum. This post-impressionist masterpiece depicts a swirling night sky over a quiet town, reflecting van Gogh's emotional state and innovative style.";
        } else if (filename.contains("scream")) {
            return "Created by Edvard Munch in " + year + ", this expressionist work captures the anxiety of the modern age. The artwork was inspired by a moment when Munch heard 'the scream of nature' during a sunset walk.";
        } else if (filename.contains("girl_pearl")) {
            return "Painted by Johannes Vermeer around " + year + " during the Dutch Golden Age. This 'Mona Lisa of the North' showcases Vermeer's mastery of light and his use of expensive ultramarine blue and pearl pigments.";
        } else if (filename.contains("american_gothic")) {
            return "Created by Grant Wood in " + year + " during the Great Depression era. This American Regionalist painting depicts rural American values and became an iconic representation of American art.";
        } else if (filename.contains("battle")) {
            return "This historical battle scene from " + year + " represents a significant moment in military history. Such paintings were often commissioned to commemorate important victories and document historical events for posterity.";
        } else if (filename.contains("death") || filename.contains("execution")) {
            return "This dramatic historical painting from " + year + " depicts a significant moment in history. Such works often served both artistic and political purposes, documenting important events while conveying moral or political messages.";
        } else if (filename.contains("declaration")) {
            return "This painting commemorates the signing of the Declaration of Independence in " + year + ", a pivotal moment in American history that established the United States as an independent nation.";
        } else {
            // Generic historical context based on time period
            if (yearInt >= 1400 && yearInt <= 1600) {
                return "Created during the Renaissance period (" + year + "), an era of renewed interest in classical art, science, and humanism that transformed European culture.";
            } else if (yearInt >= 1600 && yearInt <= 1750) {
                return "Painted during the Baroque period (" + year + "), characterized by dramatic lighting, emotional intensity, and grandeur in art and architecture.";
            } else if (yearInt >= 1750 && yearInt <= 1850) {
                return "Created during the Neoclassical period (" + year + "), when artists drew inspiration from ancient Greek and Roman art, emphasizing order, symmetry, and heroic themes.";
            } else if (yearInt >= 1850 && yearInt <= 1900) {
                return "Painted in the 19th century (" + year + ") during a time of great artistic innovation, including movements like Impressionism and Realism that challenged traditional academic art.";
            } else if (yearInt >= 1900 && yearInt <= 1950) {
                return "Created in the early 20th century (" + year + ") during a period of artistic revolution, with new movements like Modernism, Cubism, and Expressionism changing the art world.";
            } else {
                return "This artwork from " + year + " represents an important moment in art history, reflecting the cultural and social values of its time.";
            }
        }
    }
    
    private static int parseYear(String year) {
        if (year == null) return 0;
        try {
            return Integer.parseInt(year.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
