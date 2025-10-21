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

    @Test
    void testHighScoreStringFormattingAfterUpdate() {
        User user = new User(5, "player", 42, 3);

        // Update the numeric high score
        user.setHighScore(250);

        // Check both numeric and string views match the new value
        assertEquals(250, user.getHighScoreInt(),
                "getHighScoreInt() should return the updated numeric score");
        assertEquals("250", user.getBestScore(),
                "getBestScore() should return the updated score as a string");
    }

    @Test
    void testGamesPlayedCanIncrease() {
        User user = new User(7, "grinder", 10, 4);

        // Simulate another played game
        user.setGamesPlayed(user.getGamesPlayed() + 1);

        assertEquals(5, user.getGamesPlayed(),
                "Games played should reflect incremented value");
    }

    @Test
    void testToStringShouldNotBeEmpty() {
        User user = new User(9, "logUser", 300, 12);

        String s = user.toString();

        assertNotNull(s, "toString() should not return null");
        assertFalse(s.trim().isEmpty(),
                "toString() should not return an empty string");
        assertTrue(
                s.contains("logUser") || s.contains("9") || s.contains("300"),
                "toString() should include useful identifying info like username, id, or score"
        );
    }


    @Test
    void testDifferentInstancesWithSameValuesAreIndependent() {
        User u1 = new User(11, "same", 123, 2);
        User u2 = new User(11, "same", 123, 2);

        // They are not the same object
        assertNotSame(u1, u2,
                "Two different User instances with the same data should not be the same reference");

        // But their field values should match
        assertEquals(u1.getId(), u2.getId(), "IDs should match");
        assertEquals(u1.getUsername(), u2.getUsername(), "Usernames should match");
        assertEquals(u1.getBestScore(), u2.getBestScore(), "Best scores should match");
        assertEquals(u1.getGamesPlayed(), u2.getGamesPlayed(), "Games played should match");
    }

    @Test
    void testReferenceIdentityIsPreserved() {
        User u = new User(100, "identityUser", 999, 77);

        User sameRef = u; // same object, different variable

        assertSame(u, sameRef,
                "Two references pointing to the same User instance should be the same object");
        assertEquals(u.getUsername(), sameRef.getUsername(),
                "Both references should see the same username");
        assertEquals(u.getHighScoreInt(), sameRef.getHighScoreInt(),
                "Both references should see the same high score");
    }
}
