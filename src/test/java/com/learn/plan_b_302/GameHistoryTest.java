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
    @DisplayName("Should format accuracy correctly")
    void testGetFormattedAccuracy() {
        GameHistory history = new GameHistory(
            123, "YEAR_GAME", 7, 10, 10, 7, testDate, "{}"
        );
        
        assertEquals("7/10 (70.0%)", history.getFormattedAccuracy(), 
            "Should format accuracy with correct format");
    }

    @Test
    @DisplayName("Should provide game type display")
    void testGetGameTypeDisplay() {
        GameHistory yearHistory = new GameHistory(
            123, "YEAR_GAME", 8, 10, 10, 8, testDate, "{}"
        );
        
        assertEquals("📅 Year Game", yearHistory.getGameTypeDisplay(), 
            "Should display formatted year game type");
    }

    @Test
    @DisplayName("Should set default ID (e.g. 0) when not provided in constructor")
    void testDefaultIdWhenNotProvided() {
        GameHistory history = new GameHistory(
                999, "YEAR_GAME", 10, 10, 10, 10, testDate, "{}"
        );

        // In most implementations, the no-id constructor leaves id at 0.
        // This documents that behaviour.
        assertEquals(0, history.getId(),
                "When no explicit ID is provided, getId() should return default (expected 0)");
    }

    @Test
    @DisplayName("Accuracy should be 0.0 if no questions were answered (edge case)")
    void testAccuracyWithZeroQuestionsAnswered() {
        GameHistory history = new GameHistory(
                123, "YEAR_GAME", 0, 10, 0, 0, testDate, "{}"
        );

        double accuracy = history.getAccuracyPercentage();
        assertEquals(0.0, accuracy, 0.0001,
                "If 0/0, accuracy should safely be 0.0 and not NaN or Infinity");
    }

    @Test
    @DisplayName("Formatted accuracy should handle 0/0 gracefully")
    void testFormattedAccuracyWithZeroQuestionsAnswered() {
        GameHistory history = new GameHistory(
                123, "YEAR_GAME", 0, 10, 0, 0, testDate, "{}"
        );

        String formatted = history.getFormattedAccuracy();
        assertEquals("0/0 (0.0%)", formatted,
                "Formatted accuracy should show 0/0 (0.0%) when nothing answered");
    }

    @Test
    @DisplayName("Game type display should describe location game in a user-friendly way")
    void testGetGameTypeDisplayForLocationGame() {
        GameHistory locationHistory = new GameHistory(
                456, "LOCATION_GAME", 25, 30, 10, 8, testDate, "{}"
        );

        String display = locationHistory.getGameTypeDisplay();

        // We keep this flexible because emoji text might vary,
        // but it should still clearly communicate "Location Game".
        assertNotNull(display, "Display text should not be null");
        assertFalse(display.isEmpty(), "Display text should not be empty");
        assertTrue(display.toLowerCase().contains("location"),
                "Display text should mention 'Location'");
        assertTrue(display.toLowerCase().contains("game"),
                "Display text should mention 'Game'");
    }

    @Test
    @DisplayName("Should allow null gameDetails without throwing")
    void testNullGameDetailsAllowed() {
        GameHistory history = new GameHistory(
                789, "YEAR_GAME", 5, 10, 10, 5, testDate, null
        );

        // Construction shouldn't throw, and getter should reflect null
        assertDoesNotThrow(() -> history.getGameDetails(),
                "Calling getGameDetails() on null details should not throw");
        assertNull(history.getGameDetails(),
                "If null was passed for gameDetails, getter should return null");
    }

    @Test
    @DisplayName("toString() should return a non-empty summary for logging/debugging")
    void testToStringNotEmpty() {
        GameHistory history = new GameHistory(
                42, 123, "YEAR_GAME", 9, 10, 10, 9, testDate,
                "{\"results\":[]}"
        );

        String s = history.toString();
        assertNotNull(s, "toString() should not return null");
        assertFalse(s.trim().isEmpty(), "toString() should not return empty string");
        assertTrue(s.contains("YEAR_GAME") || s.contains("123") || s.contains("42"),
                "toString() should include some identifying info like game type or IDs");
    }
}