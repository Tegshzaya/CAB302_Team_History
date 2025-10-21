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
    @DisplayName("Should handle setters correctly")
    void testSetters() {
        UserGameRecord record = new UserGameRecord(123, "YEAR_GAME", 8, testDate);
        
        record.setId(2);
        record.setScore(20);
        
        assertEquals(2, record.getId(), "ID should be updated");
        assertEquals(20, record.getScore(), "Score should be updated");
    }
}