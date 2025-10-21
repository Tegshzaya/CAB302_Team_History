package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for application components
 */
class IntegrationTest {

    @Test
    @DisplayName("Should validate User class integration")
    void testUserClassIntegration() {
        // Test User creation and basic operations
        User user = new User(1, "testuser", 100, 5);
        
        assertNotNull(user, "User should not be null");
        assertEquals(1, user.getId(), "User ID should match");
        assertEquals("testuser", user.getUsername(), "Username should match");
        assertEquals("100", user.getBestScore(), "Best score should match");
        assertEquals(5, user.getGamesPlayed(), "Games played should match");
        
        // Test UserManager integration
        UserManager.setCurrentUser(user);
        User retrievedUser = UserManager.getCurrentUser();
        
        assertNotNull(retrievedUser, "Retrieved user should not be null");
        assertEquals(user.getId(), retrievedUser.getId(), "Retrieved user ID should match");
        assertEquals(user.getUsername(), retrievedUser.getUsername(), "Retrieved username should match");
        
        // Clean up
        UserManager.clearCurrentUser();
        assertNull(UserManager.getCurrentUser(), "User should be cleared");
    }

    @Test
    @DisplayName("Should validate ArtImage class integration")
    void testArtImageClassIntegration() {
        // Test ArtImage creation
        ArtImage image = new ArtImage(
            "/test/path.jpg",
            "1503",
            "1503", "1495", "1510", "1520"
        );
        
        assertNotNull(image, "ArtImage should not be null");
        assertEquals("/test/path.jpg", image.getPath(), "Path should match");
        assertEquals("1503", image.getCorrectAnswer(), "Correct answer should match");
        
        // Test options
        java.util.List<String> options = image.getAllOptions();
        assertNotNull(options, "Options should not be null");
        assertEquals(4, options.size(), "Should have 4 options");
        assertTrue(options.contains("1503"), "Should contain correct answer");
        
        // Test answer validation
        assertTrue(image.isCorrectAnswer("1503"), "Should recognize correct answer");
        assertFalse(image.isCorrectAnswer("1495"), "Should not recognize wrong answer");
    }

    @Test
    @DisplayName("Should validate UserGameRecord class integration")
    void testUserGameRecordClassIntegration() {
        java.time.LocalDateTime testDate = java.time.LocalDateTime.of(2025, 10, 21, 15, 30);
        
        // Test UserGameRecord creation
        UserGameRecord record = new UserGameRecord(1, 123, "YEAR_GAME", 8, testDate);
        
        assertNotNull(record, "UserGameRecord should not be null");
        assertEquals(1, record.getId(), "ID should match");
        assertEquals(123, record.getUserId(), "User ID should match");
        assertEquals("YEAR_GAME", record.getGameType(), "Game type should match");
        assertEquals(8, record.getScore(), "Score should match");
        assertEquals(testDate, record.getDatePlayed(), "Date should match");
        
        // Test display methods
        assertEquals("📅 Year Game", record.getGameTypeDisplay(), "Game type display should match");
        assertNotNull(record.getFormattedDate(), "Formatted date should not be null");
    }

    @Test
    @DisplayName("Should validate GameHistory class integration")
    void testGameHistoryClassIntegration() {
        java.time.LocalDateTime testDate = java.time.LocalDateTime.of(2025, 10, 21, 15, 30);
        String gameDetails = "{\"results\":[{\"question\":1,\"correct\":true}]}";
        
        // Test GameHistory creation
        GameHistory history = new GameHistory(
            1, 123, "YEAR_GAME", 8, 10, 10, 8, testDate, gameDetails
        );
        
        assertNotNull(history, "GameHistory should not be null");
        assertEquals(1, history.getId(), "ID should match");
        assertEquals(123, history.getUserId(), "User ID should match");
        assertEquals("YEAR_GAME", history.getGameType(), "Game type should match");
        assertEquals(8, history.getScore(), "Score should match");
        assertEquals(10, history.getMaxPossibleScore(), "Max score should match");
        assertEquals(10, history.getQuestionsAnswered(), "Questions answered should match");
        assertEquals(8, history.getCorrectAnswers(), "Correct answers should match");
        assertEquals(testDate, history.getPlayedAt(), "Date should match");
        assertEquals(gameDetails, history.getGameDetails(), "Details should match");
        
        // Test calculation methods
        assertEquals(80.0, history.getAccuracyPercentage(), 0.01, "Accuracy should be 80%");
        assertEquals("8/10 (80.0%)", history.getFormattedAccuracy(), "Formatted accuracy should match");
        assertNotNull(history.getFormattedDate(), "Formatted date should not be null");
        assertEquals("📅 Year Game", history.getGameTypeDisplay(), "Game type display should match");
    }

    @Test
    @DisplayName("Should validate UserManager integration")
    void testUserManagerIntegration() {
        // Test initial state
        assertNull(UserManager.getCurrentUser(), "Initial user should be null");
        assertFalse(UserManager.hasCurrentUser(), "Should not have current user initially");
        
        // Test setting user
        User testUser = new User(1, "testuser", 100, 5);
        UserManager.setCurrentUser(testUser);
        
        assertNotNull(UserManager.getCurrentUser(), "Current user should not be null");
        assertTrue(UserManager.hasCurrentUser(), "Should have current user");
        assertEquals(testUser, UserManager.getCurrentUser(), "Current user should match set user");
        
        // Test clearing user
        UserManager.clearCurrentUser();
        assertNull(UserManager.getCurrentUser(), "User should be cleared");
        assertFalse(UserManager.hasCurrentUser(), "Should not have current user after clearing");
    }

    @Test
    @DisplayName("Should validate data flow integration")
    void testDataFlowIntegration() {
        // Test complete data flow
        User user = new User(1, "testuser", 100, 5);
        UserManager.setCurrentUser(user);
        
        // Create game record
        java.time.LocalDateTime testDate = java.time.LocalDateTime.of(2025, 10, 21, 15, 30);
        UserGameRecord record = new UserGameRecord(1, user.getId(), "YEAR_GAME", 8, testDate);
        
        // Create game history
        String gameDetails = "{\"results\":[{\"question\":1,\"correct\":true}]}";
        GameHistory history = new GameHistory(
            1, user.getId(), "YEAR_GAME", 8, 10, 10, 8, testDate, gameDetails
        );
        
        // Validate data consistency
        assertEquals(user.getId(), record.getUserId(), "Record user ID should match user ID");
        assertEquals(user.getId(), history.getUserId(), "History user ID should match user ID");
        assertEquals(record.getGameType(), history.getGameType(), "Game types should match");
        assertEquals(record.getScore(), history.getScore(), "Scores should match");
        
        // Clean up
        UserManager.clearCurrentUser();
    }

    @Test
    @DisplayName("Should validate error handling integration")
    void testErrorHandlingIntegration() {
        // Test null handling
        assertDoesNotThrow(() -> {
            UserManager.setCurrentUser(null);
            UserManager.clearCurrentUser();
        }, "Null user handling should not throw");
    }
}
