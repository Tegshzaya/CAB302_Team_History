package com.learn.plan_b_302;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Test class for DatabaseManager functionality
 */
public class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        // Initialize database for testing
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
    }

    @Test
    void testDatabaseInitialization() {
        // Test that database initializes without errors
        assertDoesNotThrow(() -> {
            DatabaseManager manager = new DatabaseManager();
            manager.initializeDatabase();
        });
    }

    @Test
    void testGetAllImages() {
        // Test that we can retrieve art images
        List<ArtImage> images = DatabaseManager.getAllImages();
        assertNotNull(images, "Images list should not be null");
        assertFalse(images.isEmpty(), "Images list should not be empty");
    }

    @Test
    void testCreateUser() {
        // Test user creation
        String username = "testuser" + System.currentTimeMillis();
        String password = "testpass123";
        
        boolean created = dbManager.createUser(username, password);
        assertTrue(created, "User creation should succeed");
    }

    @Test
    void testAuthenticateUser() {
        // Test user authentication
        String username = "testuser" + System.currentTimeMillis();
        String password = "testpass123";
        
        // Create user first
        dbManager.createUser(username, password);
        
        // Test authentication
        User user = dbManager.authenticateUser(username, password);
        assertNotNull(user, "Authenticated user should not be null");
        assertEquals(username, user.getUsername(), "Username should match");
    }

    @Test
    void testUpdateUserScore() {
        // Test score updating
        String username = "testuser" + System.currentTimeMillis();
        String password = "testpass123";
        
        // Create user first
        dbManager.createUser(username, password);
        User user = dbManager.authenticateUser(username, password);
        
        // Update score
        assertDoesNotThrow(() -> {
            DatabaseManager.updateUserScore(user.getId(), 5, 10);
        });
        
        // Verify score was updated
        User updatedUser = DatabaseManager.refreshUser(user.getId());
        assertNotNull(updatedUser, "Updated user should not be null");
    }
}
