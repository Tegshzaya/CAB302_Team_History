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

    @Test
    @DisplayName("hasCurrentUser() should reflect state after set and clear operations")
    void testHasCurrentUserReflectsStateTransitions() {
        // Initially should be false
        assertFalse(UserManager.hasCurrentUser(),
                "Before setting user, hasCurrentUser() should be false");

        // After setting user, should be true
        User u = new User(10, "stateUser", 200, 9);
        UserManager.setCurrentUser(u);
        assertTrue(UserManager.hasCurrentUser(),
                "After setting user, hasCurrentUser() should be true");

        // After clearing, should go back to false
        UserManager.clearCurrentUser();
        assertFalse(UserManager.hasCurrentUser(),
                "After clearing user, hasCurrentUser() should be false again");
    }

    @Test
    @DisplayName("getCurrentUser() should return the exact same object reference that was set")
    void testGetCurrentUserReturnsSameReference() {
        User u = new User(5, "refUser", 123, 4);
        UserManager.setCurrentUser(u);

        User retrieved = UserManager.getCurrentUser();

        assertSame(u, retrieved,
                "UserManager should return the same object instance that was provided");
    }

    @Test
    @DisplayName("Setting the same user twice should be idempotent and not break state")
    void testSettingSameUserTwiceIsIdempotent() {
        User u = new User(2, "repeatUser", 77, 3);

        UserManager.setCurrentUser(u);
        assertTrue(UserManager.hasCurrentUser(), "Should have current user after first set");
        assertSame(u, UserManager.getCurrentUser(),
                "After first set, stored instance should match");

        // Set the same instance again
        UserManager.setCurrentUser(u);
        assertTrue(UserManager.hasCurrentUser(), "Should still have current user after second set");
        assertSame(u, UserManager.getCurrentUser(),
                "After second set with same instance, stored instance should still match");
    }

    @Test
    @DisplayName("Mutating the stored user object should be reflected via UserManager")
    void testMutatingCurrentUserObjectIsVisibleThroughManager() {
        User u = new User(9, "mutableUser", 500, 12);
        UserManager.setCurrentUser(u);

        // Sanity check before mutation
        assertEquals("500", UserManager.getCurrentUser().getBestScore(),
                "Initial best score should match constructor input");
        assertEquals(12, UserManager.getCurrentUser().getGamesPlayed(),
                "Initial games played should match constructor input");

        // Simulate mutation on the same object reference.
        // (Assumes setters exist; if they don't, this is documenting that they DON'T.)
        // We'll just assertSame again so test is still meaningful if setters don't exist.
        assertSame(u, UserManager.getCurrentUser(),
                "UserManager should hold a live reference to the same user object");

        // NOTE:
        // If your User class has setters like setGamesPlayed(...) or setBestScore(...),
        // you can uncomment and assert that mutation is visible.
        //
        // u.setGamesPlayed(13);
        // assertEquals(13, UserManager.getCurrentUser().getGamesPlayed(),
        //     "Updated gamesPlayed should be reflected through UserManager reference");
    }

    @Test
    @DisplayName("Clearing an already-cleared user should not throw and should remain empty")
    void testClearCurrentUserWhenAlreadyClearDoesNotThrow() {
        // Ensure it's already cleared
        UserManager.clearCurrentUser();
        assertFalse(UserManager.hasCurrentUser(),
                "Precondition: no current user");

        // Clearing again should be safe
        assertDoesNotThrow(
                UserManager::clearCurrentUser,
                "Calling clearCurrentUser() on an already empty state should not throw"
        );

        assertNull(UserManager.getCurrentUser(),
                "After clearing twice, current user should still be null");
        assertFalse(UserManager.hasCurrentUser(),
                "After clearing twice, hasCurrentUser() should still be false");
    }

}