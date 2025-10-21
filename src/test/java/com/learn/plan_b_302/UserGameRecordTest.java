package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Test class for UserGameRecord functionality
 */
class UserGameRecordTest {

    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.of(2025, 10, 21, 15, 30);
    }

    @Test
    @DisplayName("Should create UserGameRecord with all parameters")
    void testUserGameRecordCreation() {
        UserGameRecord record = new UserGameRecord(1, 123, "YEAR_GAME", 8, testDate);
        
        assertEquals(1, record.getId(), "ID should match");
        assertEquals(123, record.getUserId(), "User ID should match");
        assertEquals("YEAR_GAME", record.getGameType(), "Game type should match");
        assertEquals(8, record.getScore(), "Score should match");
        assertEquals(testDate, record.getDatePlayed(), "Date should match");
    }

    @Test
    @DisplayName("Should create UserGameRecord without ID")
    void testUserGameRecordCreationWithoutId() {
        UserGameRecord record = new UserGameRecord(123, "LOCATION_GAME", 25, testDate);
        
        assertEquals(123, record.getUserId(), "User ID should match");
        assertEquals("LOCATION_GAME", record.getGameType(), "Game type should match");
        assertEquals(25, record.getScore(), "Score should match");
        assertEquals(testDate, record.getDatePlayed(), "Date should match");
    }

    @Test
    @DisplayName("Should provide correct game type display for year game")
    void testGetGameTypeDisplayYearGame() {
        UserGameRecord record = new UserGameRecord(123, "YEAR_GAME", 8, testDate);
        
        assertEquals("📅 Year Game", record.getGameTypeDisplay(), 
            "Should display formatted year game type");
    }

    @Test
    @DisplayName("Should provide correct game type display for location game")
    void testGetGameTypeDisplayLocationGame() {
        UserGameRecord record = new UserGameRecord(123, "LOCATION_GAME", 25, testDate);
        
        assertEquals("🌍 Location Game", record.getGameTypeDisplay(), 
            "Should display formatted location game type");
    }

    @Test
    @DisplayName("Should provide default game type display for unknown type")
    void testGetGameTypeDisplayUnknown() {
        UserGameRecord record = new UserGameRecord(123, "UNKNOWN_GAME", 10, testDate);
        
        assertEquals("UNKNOWN_GAME", record.getGameTypeDisplay(), 
            "Should return original type for unknown game");
    }

    @Test
    @DisplayName("Should format date correctly")
    void testGetFormattedDate() {
        UserGameRecord record = new UserGameRecord(123, "YEAR_GAME", 8, testDate);
        
        assertEquals("Oct 21, 15:30", record.getFormattedDate(), 
            "Should format date as 'MMM dd, HH:mm'");
    }

    @Test
    @DisplayName("Should provide correct score display for year game")
    void testGetScoreDisplayYearGame() {
        UserGameRecord record = new UserGameRecord(123, "YEAR_GAME", 8, testDate);
        
        assertEquals("8/10", record.getScoreDisplay(), 
            "Should display year game score as 'x/10'");
    }

    @Test
    @DisplayName("Should provide correct score display for location game")
    void testGetScoreDisplayLocationGame() {
        UserGameRecord record = new UserGameRecord(123, "LOCATION_GAME", 25, testDate);
        
        assertEquals("25/30", record.getScoreDisplay(), 
            "Should display location game score as 'x/30'");
    }

    @Test
    @DisplayName("Should provide default score display for unknown game type")
    void testGetScoreDisplayUnknown() {
        UserGameRecord record = new UserGameRecord(123, "UNKNOWN_GAME", 15, testDate);
        
        assertEquals("15", record.getScoreDisplay(), 
            "Should display raw score for unknown game type");
    }

    @Test
    @DisplayName("Should handle setters correctly")
    void testSetters() {
        UserGameRecord record = new UserGameRecord(123, "YEAR_GAME", 8, testDate);
        
        record.setId(2);
        record.setUserId(456);
        record.setGameType("LOCATION_GAME");
        record.setScore(20);
        
        LocalDateTime newDate = LocalDateTime.of(2025, 10, 22, 10, 15);
        record.setDatePlayed(newDate);
        
        assertEquals(2, record.getId(), "ID should be updated");
        assertEquals(456, record.getUserId(), "User ID should be updated");
        assertEquals("LOCATION_GAME", record.getGameType(), "Game type should be updated");
        assertEquals(20, record.getScore(), "Score should be updated");
        assertEquals(newDate, record.getDatePlayed(), "Date should be updated");
    }

    @Test
    @DisplayName("Should handle zero and negative scores")
    void testEdgeCaseScores() {
        UserGameRecord recordZero = new UserGameRecord(123, "YEAR_GAME", 0, testDate);
        UserGameRecord recordNegative = new UserGameRecord(123, "LOCATION_GAME", -1, testDate);
        
        assertEquals("0/10", recordZero.getScoreDisplay(), "Should handle zero score");
        assertEquals("-1/30", recordNegative.getScoreDisplay(), "Should handle negative score");
    }

    @Test
    @DisplayName("Should provide meaningful toString representation")
    void testToString() {
        UserGameRecord record = new UserGameRecord(1, 123, "YEAR_GAME", 8, testDate);
        
        String toString = record.toString();
        
        assertNotNull(toString, "toString should not be null");
        assertTrue(toString.contains("UserGameRecord"), "Should contain class name");
        assertTrue(toString.contains("123"), "Should contain user ID");
        assertTrue(toString.contains("YEAR_GAME"), "Should contain game type");
        assertTrue(toString.contains("8"), "Should contain score");
    }
}