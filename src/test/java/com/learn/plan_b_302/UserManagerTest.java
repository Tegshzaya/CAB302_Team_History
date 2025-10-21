package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UserManager functionality
 */
class UserManagerTest {

    @BeforeEach
    void setUp() {
        // Clear any existing user before each test
        UserManager.clearCurrentUser();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        UserManager.clearCurrentUser();
    }

    @Test
    @DisplayName("Should initially have no current user")
    void testInitialState() {
        assertNull(UserManager.getCurrentUser(), "Should initially have no current user");
        assertFalse(UserManager.hasCurrentUser(), "Should initially return false for hasCurrentUser");
    }

    @Test
    @DisplayName("Should set and get current user correctly")
    void testSetAndGetCurrentUser() {
        User testUser = new User(1, "testuser", 100, 5);
        
        UserManager.setCurrentUser(testUser);
        
        assertEquals(testUser, UserManager.getCurrentUser(), "Should return the set user");
        assertTrue(UserManager.hasCurrentUser(), "Should return true for hasCurrentUser");
    }

    @Test
    @DisplayName("Should clear current user correctly")
    void testClearCurrentUser() {
        User testUser = new User(1, "testuser", 100, 5);
        UserManager.setCurrentUser(testUser);
        
        assertTrue(UserManager.hasCurrentUser(), "Should have current user before clearing");
        
        UserManager.clearCurrentUser();
        
        assertNull(UserManager.getCurrentUser(), "Should have no current user after clearing");
        assertFalse(UserManager.hasCurrentUser(), "Should return false for hasCurrentUser after clearing");
    }

    @Test
    @DisplayName("Should handle null user correctly")
    void testSetNullUser() {
        User testUser = new User(1, "testuser", 100, 5);
        UserManager.setCurrentUser(testUser);
        
        assertTrue(UserManager.hasCurrentUser(), "Should have user initially");
        
        UserManager.setCurrentUser(null);
        
        assertNull(UserManager.getCurrentUser(), "Should accept null user");
        assertFalse(UserManager.hasCurrentUser(), "Should return false for hasCurrentUser with null user");
    }

    @Test
    @DisplayName("Should replace existing user when setting new user")
    void testReplaceCurrentUser() {
        User firstUser = new User(1, "firstuser", 50, 3);
        User secondUser = new User(2, "seconduser", 150, 8);
        
        UserManager.setCurrentUser(firstUser);
        assertEquals(firstUser, UserManager.getCurrentUser(), "Should set first user");
        
        UserManager.setCurrentUser(secondUser);
        assertEquals(secondUser, UserManager.getCurrentUser(), "Should replace with second user");
        assertNotEquals(firstUser, UserManager.getCurrentUser(), "Should no longer return first user");
    }

    @Test
    @DisplayName("Should handle user with different properties")
    void testDifferentUserProperties() {
        // Test with zero values
        User zeroUser = new User(0, "", 0, 0);
        UserManager.setCurrentUser(zeroUser);
        assertEquals(zeroUser, UserManager.getCurrentUser(), "Should handle user with zero values");
    }
}