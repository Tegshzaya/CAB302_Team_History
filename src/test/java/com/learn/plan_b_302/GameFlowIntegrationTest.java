package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Integration test for complete game flow
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameFlowIntegrationTest {

    private DatabaseManager dbManager;
    private User testUser;
    private String testUsername;

    @BeforeAll
    void setUpAll() {
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        
        testUsername = "integrationTestUser_" + System.currentTimeMillis();
        dbManager.createUser(testUsername, "testpass123");
        testUser = dbManager.authenticateUser(testUsername, "testpass123");
        
        assertNotNull(testUser, "Test user should be created and authenticated");
    }

    @Test
    @DisplayName("Complete user registration and login flow")
    @Order(1)
    void testCompleteAuthenticationFlow() {
        // Test user creation
        String newUsername = "newuser_" + System.currentTimeMillis();
        boolean created = dbManager.createUser(newUsername, "password123");
        assertTrue(created, "New user should be created successfully");
        
        // Test authentication
        User newUser = dbManager.authenticateUser(newUsername, "password123");
        assertNotNull(newUser, "User should authenticate successfully");
        assertEquals(newUsername, newUser.getUsername(), "Username should match");
        
        // Test invalid authentication
        User invalidUser = dbManager.authenticateUser(newUsername, "wrongpassword");
        assertNull(invalidUser, "Invalid credentials should return null");
    }

    @Test
    @DisplayName("Complete year game flow with score tracking")
    @Order(2)
    void testCompleteYearGameFlow() {
        // Get art images for game
        List<ArtImage> images = DatabaseManager.getAllImages();
        assertNotNull(images, "Should have art images");
        assertFalse(images.isEmpty(), "Should have at least one art image");
        
        // Simulate playing a year game
        int score = 0;
        int totalQuestions = Math.min(images.size(), 10);
        
        for (int i = 0; i < totalQuestions; i++) {
            ArtImage image = images.get(i);
            
            // Simulate correct answer for testing
            String correctAnswer = image.getCorrectAnswer();
            boolean isCorrect = image.isCorrectAnswer(correctAnswer);
            assertTrue(isCorrect, "Correct answer should be recognized as correct");
            
            if (isCorrect) {
                score++;
            }
        }
        
        // Save game record
        UserGameRecord record = new UserGameRecord(
            testUser.getId(), 
            "YEAR_GAME", 
            score, 
            LocalDateTime.now()
        );
        
        assertDoesNotThrow(() -> UserGameManager.saveGameRecord(record),
            "Should save game record successfully");
        
        // Update user score
        assertDoesNotThrow(() -> DatabaseManager.updateUserScore(testUser.getId(), score, totalQuestions),
            "Should update user score successfully");
        
        // Verify score was recorded
        int bestScore = UserGameManager.getBestScore(testUser.getId(), "YEAR_GAME");
        assertEquals(score, bestScore, "Best score should match played game score");
    }

    @Test
    @DisplayName("Complete location game flow with score tracking")
    @Order(3)
    void testCompleteLocationGameFlow() {
        // Simulate playing a location game (10 locations, up to 30 points)
        int score = 0;
        int locations = 10;
        
        // Simulate getting different distances and points
        int[] distances = {5, 15, 45, 150, 300, 8, 25, 80, 200, 10}; // km
        
        for (int distance : distances) {
            int points;
            if (distance <= 10) points = 3;
            else if (distance <= 50) points = 2; 
            else if (distance <= 200) points = 1;
            else points = 0;
            
            score += points;
        }
        
        // Save location game record
        UserGameRecord record = new UserGameRecord(
            testUser.getId(), 
            "LOCATION_GAME", 
            score, 
            LocalDateTime.now()
        );
        
        assertDoesNotThrow(() -> UserGameManager.saveGameRecord(record),
            "Should save location game record successfully");
        
        // Verify score was recorded
        int bestScore = UserGameManager.getBestScore(testUser.getId(), "LOCATION_GAME");
        assertEquals(score, bestScore, "Best location score should match");
    }

    @Test
    @DisplayName("Game history tracking across multiple games")
    @Order(4)
    void testGameHistoryTracking() {
        // Play multiple games
        for (int i = 0; i < 5; i++) {
            int score = 6 + i; // Increasing scores
            UserGameRecord record = new UserGameRecord(
                testUser.getId(),
                "YEAR_GAME",
                score,
                LocalDateTime.now()
            );
            UserGameManager.saveGameRecord(record);
        }
        
        // Check recent games
        List<UserGameRecord> recentGames = UserGameManager.getRecentGames(testUser.getId(), 10);
        assertNotNull(recentGames, "Recent games should not be null");
        assertTrue(recentGames.size() >= 5, "Should have at least 5 recent games");
        
        // Check games played count
        int gamesPlayed = UserGameManager.getGamesPlayed(testUser.getId(), "YEAR_GAME");
        assertTrue(gamesPlayed >= 5, "Should have played at least 5 games");
        
        // Check best score
        int bestScore = UserGameManager.getBestScore(testUser.getId(), "YEAR_GAME");
        assertTrue(bestScore >= 10, "Best score should be at least 10 from latest game");
    }

    @Test
    @DisplayName("User statistics update correctly across game types")
    @Order(5)
    void testUserStatisticsIntegration() {
        // Get initial stats
        int initialYearGames = UserGameManager.getGamesPlayed(testUser.getId(), "YEAR_GAME");
        int initialLocationGames = UserGameManager.getGamesPlayed(testUser.getId(), "LOCATION_GAME");
        int initialTotalGames = UserGameManager.getTotalGamesPlayed(testUser.getId());
        
        // Play one of each game type
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 8, LocalDateTime.now()));
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "LOCATION_GAME", 22, LocalDateTime.now()));
        
        // Verify stats updated
        int newYearGames = UserGameManager.getGamesPlayed(testUser.getId(), "YEAR_GAME");
        int newLocationGames = UserGameManager.getGamesPlayed(testUser.getId(), "LOCATION_GAME");
        int newTotalGames = UserGameManager.getTotalGamesPlayed(testUser.getId());
        
        assertEquals(initialYearGames + 1, newYearGames, "Year games count should increase by 1");
        assertEquals(initialLocationGames + 1, newLocationGames, "Location games count should increase by 1");
        assertEquals(initialTotalGames + 2, newTotalGames, "Total games count should increase by 2");
    }

    @Test
    @DisplayName("UserManager integration with game flow")
    @Order(6)
    void testUserManagerIntegration() {
        // Clear any existing user
        UserManager.clearCurrentUser();
        assertFalse(UserManager.hasCurrentUser(), "Should start with no current user");
        
        // Set current user
        UserManager.setCurrentUser(testUser);
        assertTrue(UserManager.hasCurrentUser(), "Should have current user after setting");
        
        User currentUser = UserManager.getCurrentUser();
        assertNotNull(currentUser, "Current user should not be null");
        assertEquals(testUser.getId(), currentUser.getId(), "User ID should match");
        assertEquals(testUser.getUsername(), currentUser.getUsername(), "Username should match");
        
        // Simulate game completion with current user
        UserGameRecord record = new UserGameRecord(
            currentUser.getId(),
            "YEAR_GAME",
            7,
            LocalDateTime.now()
        );
        
        assertDoesNotThrow(() -> UserGameManager.saveGameRecord(record),
            "Should save record for current user");
        
        // Verify the game was recorded
        List<UserGameRecord> userGames = UserGameManager.getRecentGames(currentUser.getId(), 1);
        assertFalse(userGames.isEmpty(), "Should have at least one game recorded");
        assertEquals("YEAR_GAME", userGames.get(0).getGameType(), "Game type should match");
    }

    @Test
    @DisplayName("Data consistency across multiple operations")
    @Order(7)
    void testDataConsistency() {
        // Record current state
        int initialBestYearScore = UserGameManager.getBestScore(testUser.getId(), "YEAR_GAME");
        int initialYearGamesPlayed = UserGameManager.getGamesPlayed(testUser.getId(), "YEAR_GAME");
        
        // Play multiple games with different scores
        int[] scores = {3, 9, 5, 10, 7}; // 9 and 10 should be new bests
        
        for (int score : scores) {
            UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", score, LocalDateTime.now()));
        }
        
        // Verify consistency
        int newBestScore = UserGameManager.getBestScore(testUser.getId(), "YEAR_GAME");
        int newGamesPlayed = UserGameManager.getGamesPlayed(testUser.getId(), "YEAR_GAME");
        
        assertTrue(newBestScore >= Math.max(initialBestYearScore, 10), "Best score should be at least 10");
        assertEquals(initialYearGamesPlayed + scores.length, newGamesPlayed, "Games played should increase by scores length");
        
        // Verify recent games order (should be newest first)
        List<UserGameRecord> recentGames = UserGameManager.getRecentGames(testUser.getId(), scores.length);
        assertFalse(recentGames.isEmpty(), "Should have recent games");
        
        // Check that games are ordered by date (newest first)
        if (recentGames.size() >= 2) {
            LocalDateTime first = recentGames.get(0).getDatePlayed();
            LocalDateTime second = recentGames.get(1).getDatePlayed();
            assertTrue(first.isAfter(second) || first.isEqual(second), 
                "Recent games should be ordered by date (newest first)");
        }
    }

    @AfterAll
    void cleanUp() {
        // Clean up test data if needed
        UserManager.clearCurrentUser();
    }
}