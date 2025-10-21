package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Test class for GameHistory functionality
 */
class GameHistoryTest {

    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.of(2025, 10, 21, 15, 30);
    }

    @Test
    @DisplayName("Should create GameHistory with all parameters")
    void testGameHistoryCreation() {
        String gameDetails = "{\"results\":[{\"question\":1,\"correct\":true}]}";
        
        GameHistory history = new GameHistory(
            1, 123, "YEAR_GAME", 8, 10, 10, 8, testDate, gameDetails
        );
        
        assertEquals(1, history.getId(), "ID should match");
        assertEquals(123, history.getUserId(), "User ID should match");
        assertEquals("YEAR_GAME", history.getGameType(), "Game type should match");
        assertEquals(8, history.getScore(), "Score should match");
        assertEquals(10, history.getMaxPossibleScore(), "Max score should match");
        assertEquals(10, history.getQuestionsAnswered(), "Questions answered should match");
        assertEquals(8, history.getCorrectAnswers(), "Correct answers should match");
        assertEquals(testDate, history.getPlayedAt(), "Date should match");
        assertEquals(gameDetails, history.getGameDetails(), "Details should match");
    }

    @Test
    @DisplayName("Should create GameHistory without ID")
    void testGameHistoryCreationWithoutId() {
        String gameDetails = "{\"results\":[{\"question\":1,\"correct\":false}]}";
        
        GameHistory history = new GameHistory(
            123, "LOCATION_GAME", 25, 30, 10, 8, testDate, gameDetails
        );
        
        assertEquals(123, history.getUserId(), "User ID should match");
        assertEquals("LOCATION_GAME", history.getGameType(), "Game type should match");
        assertEquals(25, history.getScore(), "Score should match");
        assertEquals(30, history.getMaxPossibleScore(), "Max score should match");
        assertEquals(10, history.getQuestionsAnswered(), "Questions answered should match");
        assertEquals(8, history.getCorrectAnswers(), "Correct answers should match");
    }

    @Test
    @DisplayName("Should calculate accuracy percentage correctly")
    void testGetAccuracyPercentage() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 8, 10, 10, 8, testDate, "{}"
        );
        
        assertEquals(80.0, history.getAccuracyPercentage(), 0.01, 
            "Should calculate 80% accuracy");
    }

    @Test
    @DisplayName("Should handle zero questions for accuracy")
    void testGetAccuracyPercentageZeroQuestions() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 0, 10, 0, 0, testDate, "{}"
        );
        
        assertEquals(0.0, history.getAccuracyPercentage(), 0.01, 
            "Should return 0% for zero questions");
    }

    @Test
    @DisplayName("Should format accuracy correctly")
    void testGetFormattedAccuracy() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 7, 10, 10, 7, testDate, "{}"
        );
        
        assertEquals("7/10 (70.0%)", history.getFormattedAccuracy(), 
            "Should format accuracy with correct format");
    }

    @Test
    @DisplayName("Should format date correctly")
    void testGetFormattedDate() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 8, 10, 10, 8, testDate, "{}"
        );
        
        assertEquals("Oct 21, 2025 15:30", history.getFormattedDate(), 
            "Should format date as 'MMM dd, yyyy HH:mm'");
    }

    @Test
    @DisplayName("Should provide game type display")
    void testGetGameTypeDisplay() {
        GameHistory yearHistory = new GameHistory(
            123, "YEAR_GAME", 8, 10, 10, 8, testDate, "{}"
        );
        GameHistory locationHistory = new GameHistory(
            123, "LOCATION_GAME", 25, 30, 10, 8, testDate, "{}"
        );
        
        assertEquals("📅 Year Game", yearHistory.getGameTypeDisplay(), 
            "Should display formatted year game type");
        assertEquals("🌍 Location Game", locationHistory.getGameTypeDisplay(), 
            "Should display formatted location game type");
    }

    @Test
    @DisplayName("Should handle setters correctly")
    void testSetters() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 8, 10, 10, 8, testDate, "{}"
        );
        
        history.setId(2);
        history.setScore(9);
        history.setCorrectAnswers(9);
        
        LocalDateTime newDate = LocalDateTime.of(2025, 10, 22, 10, 15);
        history.setPlayedAt(newDate);
        
        assertEquals(2, history.getId(), "ID should be updated");
        assertEquals(9, history.getScore(), "Score should be updated");
        assertEquals(9, history.getCorrectAnswers(), "Correct answers should be updated");
        assertEquals(newDate, history.getPlayedAt(), "Date should be updated");
    }

    @Test
    @DisplayName("Should handle perfect score")
    void testPerfectScore() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 10, 10, 10, 10, testDate, "{}"
        );
        
        assertEquals(100.0, history.getAccuracyPercentage(), 0.01, 
            "Should calculate 100% accuracy");
        assertEquals("10/10 (100.0%)", history.getFormattedAccuracy(), 
            "Should format perfect percentage");
    }

    @Test
    @DisplayName("Should handle zero score")
    void testZeroScore() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 0, 10, 10, 0, testDate, "{}"
        );
        
        assertEquals(0.0, history.getAccuracyPercentage(), 0.01, 
            "Should calculate 0% accuracy");
        assertEquals("0/10 (0.0%)", history.getFormattedAccuracy(), 
            "Should format zero percentage");
    }

    @Test
    @DisplayName("Should handle null game details")
    void testNullGameDetails() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 8, 10, 10, 8, testDate, null
        );
        
        assertNull(history.getGameDetails(), "Game details should remain null");
    }
}