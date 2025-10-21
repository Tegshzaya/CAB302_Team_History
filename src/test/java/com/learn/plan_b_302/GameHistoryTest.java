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
}