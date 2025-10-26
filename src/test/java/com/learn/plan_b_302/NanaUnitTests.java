package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Nana unit tests for the art game application
* for developing and testing
 * These tests cover basic functionality and are designed to be simple and clear
 */
class NanaUnitTests {

    // ========== USER CLASS TESTS ==========
    
    @Test
    @DisplayName("Test User creation with basic constructor")
    void testUserBasicCreation() {
        User user = new User(1, "testuser", 85);
        
        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals(85, user.getHighScore());
        assertEquals(0, user.getGamesPlayed());
    }
    
    @Test
    @DisplayName("Test User creation with full constructor")
    void testUserFullCreation() {
        User user = new User(2, "gamer123", 95, 10);
        
        assertEquals(2, user.getId());
        assertEquals("gamer123", user.getUsername());
        assertEquals(95, user.getHighScore());
        assertEquals(10, user.getGamesPlayed());
    }
    
    @Test
    @DisplayName("Test User setter methods")
    void testUserSetters() {
        User user = new User(1, "testuser", 50);
        
        user.setHighScore(100);
        user.setGamesPlayed(5);
        
        assertEquals(100, user.getHighScore());
        assertEquals(5, user.getGamesPlayed());
    }
    
    @Test
    @DisplayName("Test User score display methods")
    void testUserScoreDisplays() {
        User user = new User(1, "testuser", 8);
        
        assertEquals("8", user.getBestScore());
        assertEquals(8, user.getHighScoreInt());
        assertEquals("8/10", user.getYearBestScoreString());
        assertEquals("24/30", user.getLocationBestScoreString());
    }
    
    @Test
    @DisplayName("Test User toString method")
    void testUserToString() {
        User user = new User(1, "testuser", 75, 3);
        String expected = "User{id=1, username='testuser', highScore=75, gamesPlayed=3}";
        
        assertEquals(expected, user.toString());
    }

    // ========== ARTIMAGE CLASS TESTS ==========
    
    @Test
    @DisplayName("Test ArtImage creation and basic functionality")
    void testArtImageCreation() {
        ArtImage artImage = new ArtImage("images/mona_lisa.jpg", "1503", "1500", "1505", "1510", "1495");
        
        assertEquals("images/mona_lisa.jpg", artImage.getPath());
        assertEquals("1503", artImage.getCorrectAnswer());
        assertNotNull(artImage.getAllOptions());
        assertEquals(5, artImage.getAllOptions().size());
    }
    
    @Test
    @DisplayName("Test ArtImage answer validation")
    void testArtImageAnswerValidation() {
        ArtImage artImage = new ArtImage("images/starry_night.jpg", "1889", "1885", "1890", "1895", "1880");
        
        assertTrue(artImage.isCorrectAnswer("1889"));
        assertFalse(artImage.isCorrectAnswer("1885"));
        assertFalse(artImage.isCorrectAnswer("wrong"));
    }
    
    @Test
    @DisplayName("Test ArtImage title extraction")
    void testArtImageTitleExtraction() {
        ArtImage artImage = new ArtImage("images/mona_lisa.jpg", "1503", "1500", "1505", "1510", "1495");
        
        assertEquals("Mona Lisa", artImage.getTitle());
        assertNotNull(artImage.getHistoricalContext());
        assertTrue(artImage.getHistoricalContext().contains("1503"));
    }

    // ========== USERGAMERECORD CLASS TESTS ==========
    
    @Test
    @DisplayName("Test UserGameRecord creation without ID")
    void testUserGameRecordCreationWithoutId() {
        LocalDateTime now = LocalDateTime.now();
        UserGameRecord record = new UserGameRecord(1, "YEAR_GAME", 8, now);
        
        assertEquals(1, record.getUserId());
        assertEquals("YEAR_GAME", record.getGameType());
        assertEquals(8, record.getScore());
        assertEquals(now, record.getDatePlayed());
    }
    
    @Test
    @DisplayName("Test UserGameRecord creation with ID")
    void testUserGameRecordCreationWithId() {
        LocalDateTime now = LocalDateTime.now();
        UserGameRecord record = new UserGameRecord(1, 2, "LOCATION_GAME", 25, now);
        
        assertEquals(1, record.getId());
        assertEquals(2, record.getUserId());
        assertEquals("LOCATION_GAME", record.getGameType());
        assertEquals(25, record.getScore());
        assertEquals(now, record.getDatePlayed());
    }
    
    @Test
    @DisplayName("Test UserGameRecord display methods")
    void testUserGameRecordDisplayMethods() {
        LocalDateTime now = LocalDateTime.now();
        UserGameRecord yearRecord = new UserGameRecord(1, "YEAR_GAME", 7, now);
        UserGameRecord locationRecord = new UserGameRecord(2, "LOCATION_GAME", 20, now);
        
        assertEquals("📅 Year Game", yearRecord.getGameTypeDisplay());
        assertEquals("🌍 Location Game", locationRecord.getGameTypeDisplay());
        assertEquals("7/10", yearRecord.getScoreDisplay());
        assertEquals("20/30", locationRecord.getScoreDisplay());
    }
    
    @Test
    @DisplayName("Test UserGameRecord setter methods")
    void testUserGameRecordSetters() {
        LocalDateTime now = LocalDateTime.now();
        UserGameRecord record = new UserGameRecord(1, "YEAR_GAME", 5, now);
        
        record.setId(10);
        record.setUserId(20);
        record.setGameType("LOCATION_GAME");
        record.setScore(15);
        
        assertEquals(10, record.getId());
        assertEquals(20, record.getUserId());
        assertEquals("LOCATION_GAME", record.getGameType());
        assertEquals(15, record.getScore());
    }

    // ========== UTILITY FUNCTION TESTS ==========
    
    @Test
    @DisplayName("Test basic string operations")
    void testBasicStringOperations() {
        String testString = "Hello World";
        
        assertTrue(testString.contains("World"));
        assertFalse(testString.contains("Java"));
        assertEquals("HELLO WORLD", testString.toUpperCase());
        assertEquals("hello world", testString.toLowerCase());
    }
    
    @Test
    @DisplayName("Test number operations")
    void testNumberOperations() {
        int a = 10;
        int b = 5;
        
        assertEquals(15, a + b);
        assertEquals(5, a - b);
        assertEquals(50, a * b);
        assertEquals(2, a / b);
        assertTrue(a > b);
        assertFalse(a < b);
    }
    
    @Test
    @DisplayName("Test list operations")
    void testListOperations() {
        List<String> testList = List.of("apple", "banana", "cherry");
        
        assertEquals(3, testList.size());
        assertTrue(testList.contains("banana"));
        assertFalse(testList.contains("grape"));
        assertEquals("apple", testList.get(0));
        assertEquals("cherry", testList.get(2));
    }
    
    @Test
    @DisplayName("Test null and empty checks")
    void testNullAndEmptyChecks() {
        String nullString = null;
        String emptyString = "";
        String validString = "test";
        
        assertNull(nullString);
        assertNotNull(validString);
        assertTrue(emptyString.isEmpty());
        assertFalse(validString.isEmpty());
    }
}
