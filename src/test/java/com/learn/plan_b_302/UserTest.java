package com.learn.plan_b_302;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for User functionality
 */
public class UserTest {

    @Test
    void testUserCreation() {
        // Test basic User creation
        User user = new User(1, "testuser", 100, 5);
        
        assertNotNull(user, "User should not be null");
        assertEquals(1, user.getId(), "ID should match");
        assertEquals("testuser", user.getUsername(), "Username should match");
        assertEquals("100", user.getBestScore(), "Best score should match");
        assertEquals(5, user.getGamesPlayed(), "Games played should match");
    }

    @Test
    void testUserGettersAndSetters() {
        // Test getters and setters
        User user = new User(1, "testuser", 100, 5);
        
        // Test getters
        assertEquals(1, user.getId(), "ID getter should work");
        assertEquals("testuser", user.getUsername(), "Username getter should work");
        assertEquals("100", user.getBestScore(), "Best score getter should work");
        assertEquals(5, user.getGamesPlayed(), "Games played getter should work");
        
        // Test high score conversion
        assertEquals(100, user.getHighScoreInt(), "High score int conversion should work");
    }

    @Test
    void testUserWithZeroValues() {
        // Test user with zero values
        User user = new User(0, "", 0, 0);
        
        assertEquals(0, user.getId(), "Zero ID should work");
        assertEquals("", user.getUsername(), "Empty username should work");
        assertEquals("0", user.getBestScore(), "Zero best score should work");
        assertEquals(0, user.getGamesPlayed(), "Zero games played should work");
    }

    @Test
    void testUserWithNegativeValues() {
        // Test user with negative values
        User user = new User(-1, "test", -10, -5);
        
        assertEquals(-1, user.getId(), "Negative ID should work");
        assertEquals("-10", user.getBestScore(), "Negative best score should work");
        assertEquals(-5, user.getGamesPlayed(), "Negative games played should work");
    }

    @Test
    void testUserSetters() {
        // Test setters
        User user = new User(1, "testuser", 100, 5);
        
        user.setHighScore(200);
        assertEquals(200, user.getHighScoreInt(), "High score setter should work");
        
        user.setGamesPlayed(10);
        assertEquals(10, user.getGamesPlayed(), "Games played setter should work");
    }
}
