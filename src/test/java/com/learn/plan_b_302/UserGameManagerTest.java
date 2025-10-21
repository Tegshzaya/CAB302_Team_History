package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test class for UserGameManager functionality
 */
class UserGameManagerTest {

    private DatabaseManager dbManager;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Initialize database and create test user
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        
        String username = "testuser_" + System.currentTimeMillis();
        dbManager.createUser(username, "testpass");
        testUser = dbManager.authenticateUser(username, "testpass");
    }

    @Test
    @DisplayName("Should save game record successfully")
    void testSaveGameRecord() {
        UserGameRecord record = new UserGameRecord(
            testUser.getId(), 
            "YEAR_GAME", 
            8, 
            LocalDateTime.now()
        );
        
        assertDoesNotThrow(() -> UserGameManager.saveGameRecord(record),
            "Should save game record without throwing exception");
    }

    @Test
    @DisplayName("Should retrieve recent games for user")
    void testGetRecentGames() {
        // Save some test records
        UserGameRecord record1 = new UserGameRecord(testUser.getId(), "YEAR_GAME", 7, LocalDateTime.now());
        UserGameRecord record2 = new UserGameRecord(testUser.getId(), "LOCATION_GAME", 15, LocalDateTime.now());
        
        UserGameManager.saveGameRecord(record1);
        UserGameManager.saveGameRecord(record2);
        
        List<UserGameRecord> recentGames = UserGameManager.getRecentGames(testUser.getId(), 5);
        
        assertNotNull(recentGames, "Recent games should not be null");
        assertTrue(recentGames.size() >= 2, "Should have at least 2 recent games");
    }

    @Test
    @DisplayName("Should get best score for game type")
    void testGetBestScore() {
        // Save some test records with different scores
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 5, LocalDateTime.now()));
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 8, LocalDateTime.now()));
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 6, LocalDateTime.now()));
        
        int bestScore = UserGameManager.getBestScore(testUser.getId(), "YEAR_GAME");
        assertEquals(8, bestScore, "Should return the highest score");
    }

    @Test
    @DisplayName("Should get games played count for game type")
    void testGetGamesPlayed() {
        // Save multiple records
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 5, LocalDateTime.now()));
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 8, LocalDateTime.now()));
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "LOCATION_GAME", 20, LocalDateTime.now()));
        
        int yearGamesPlayed = UserGameManager.getGamesPlayed(testUser.getId(), "YEAR_GAME");
        int locationGamesPlayed = UserGameManager.getGamesPlayed(testUser.getId(), "LOCATION_GAME");
        
        assertEquals(2, yearGamesPlayed, "Should have 2 year games played");
        assertEquals(1, locationGamesPlayed, "Should have 1 location game played");
    }

    @Test
    @DisplayName("Should get total games played across all types")
    void testGetTotalGamesPlayed() {
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 5, LocalDateTime.now()));
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "LOCATION_GAME", 20, LocalDateTime.now()));
        UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", 7, LocalDateTime.now()));
        
        int totalGames = UserGameManager.getTotalGamesPlayed(testUser.getId());
        assertEquals(3, totalGames, "Should have 3 total games played");
    }

    @Test
    @DisplayName("Should return zero for non-existent user")
    void testGetScoresForNonExistentUser() {
        int bestScore = UserGameManager.getBestScore(99999, "YEAR_GAME");
        int gamesPlayed = UserGameManager.getGamesPlayed(99999, "YEAR_GAME");
        
        assertEquals(0, bestScore, "Should return 0 for non-existent user");
        assertEquals(0, gamesPlayed, "Should return 0 for non-existent user");
    }

    @Test
    @DisplayName("Should handle empty recent games list")
    void testGetRecentGamesEmpty() {
        List<UserGameRecord> recentGames = UserGameManager.getRecentGames(99999, 5);
        
        assertNotNull(recentGames, "Recent games should not be null");
        assertTrue(recentGames.isEmpty(), "Recent games should be empty for non-existent user");
    }

    @Test
    @DisplayName("Should limit recent games to specified count")
    void testGetRecentGamesLimit() {
        // Save more records than limit
        for (int i = 0; i < 10; i++) {
            UserGameManager.saveGameRecord(new UserGameRecord(testUser.getId(), "YEAR_GAME", i, LocalDateTime.now()));
        }
        
        List<UserGameRecord> recentGames = UserGameManager.getRecentGames(testUser.getId(), 3);
        
        assertNotNull(recentGames, "Recent games should not be null");
        assertEquals(3, recentGames.size(), "Should limit to 3 games");
    }

    @Test
    @DisplayName("Should update user current stats")
    void testUpdateUsersCurrentStats() {
        assertDoesNotThrow(() -> UserGameManager.updateUsersCurrentStats(testUser.getId()),
            "Should update user stats without throwing exception");
    }
}