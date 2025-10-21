package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Performance tests for database operations and user management
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerformanceTest {

    private DatabaseManager dbManager;
    private User testUser;

    @BeforeAll
    void setUpAll() {
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        
        String testUsername = "perfTestUser_" + System.currentTimeMillis();
        dbManager.createUser(testUsername, "testpass123");
        testUser = dbManager.authenticateUser(testUsername, "testpass123");
        
        assertNotNull(testUser, "Test user should be created for performance testing");
    }

    @Test
    @DisplayName("Database connection initialization performance")
    void testDatabaseInitializationPerformance() {
        long startTime = System.currentTimeMillis();
        
        DatabaseManager newManager = new DatabaseManager();
        newManager.initializeDatabase();
        
        long endTime = System.currentTimeMillis();
        long initTime = endTime - startTime;
        
        assertTrue(initTime < 1000, "Database initialization should take less than 1 second, took: " + initTime + "ms");
    }

    @Test
    @DisplayName("User authentication performance")
    void testAuthenticationPerformance() {
        // Warm up
        for (int i = 0; i < 5; i++) {
            dbManager.authenticateUser(testUser.getUsername(), "testpass123");
        }
        
        // Test authentication performance
        long startTime = System.nanoTime();
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            User user = dbManager.authenticateUser(testUser.getUsername(), "testpass123");
            assertNotNull(user, "Authentication should succeed");
        }
        
        long endTime = System.nanoTime();
        long avgTimeMs = (endTime - startTime) / (iterations * 1_000_000);
        
        assertTrue(avgTimeMs < 10, "Average authentication time should be less than 10ms, was: " + avgTimeMs + "ms");
    }

    @Test
    @DisplayName("Game record save performance")
    void testGameRecordSavePerformance() {
        // Test saving multiple game records quickly
        long startTime = System.nanoTime();
        int recordCount = 100;
        
        for (int i = 0; i < recordCount; i++) {
            UserGameRecord record = new UserGameRecord(
                testUser.getId(),
                "YEAR_GAME",
                5 + (i % 6), // Score between 5-10
                LocalDateTime.now()
            );
            
            assertDoesNotThrow(() -> UserGameManager.saveGameRecord(record),
                "Game record save should not throw exception");
        }
        
        long endTime = System.nanoTime();
        long avgTimeMs = (endTime - startTime) / (recordCount * 1_000_000);
        
        assertTrue(avgTimeMs < 5, "Average game record save time should be less than 5ms, was: " + avgTimeMs + "ms");
        
        // Verify all records were saved
        int totalGames = UserGameManager.getTotalGamesPlayed(testUser.getId());
        assertTrue(totalGames >= recordCount, "All game records should be saved");
    }

    @Test
    @DisplayName("Image retrieval performance")
    void testImageRetrievalPerformance() {
        // Test getting all images multiple times
        long startTime = System.nanoTime();
        int iterations = 50;
        
        for (int i = 0; i < iterations; i++) {
            List<ArtImage> images = DatabaseManager.getAllImages();
            assertNotNull(images, "Images should not be null");
            assertFalse(images.isEmpty(), "Should have at least one image");
        }
        
        long endTime = System.nanoTime();
        long avgTimeMs = (endTime - startTime) / (iterations * 1_000_000);
        
        assertTrue(avgTimeMs < 20, "Average image retrieval time should be less than 20ms, was: " + avgTimeMs + "ms");
    }

    @Test
    @DisplayName("Statistics retrieval performance")
    void testStatisticsRetrievalPerformance() {
        // Add some game records first
        for (int i = 0; i < 20; i++) {
            UserGameManager.saveGameRecord(new UserGameRecord(
                testUser.getId(),
                i % 2 == 0 ? "YEAR_GAME" : "LOCATION_GAME",
                5 + (i % 6),
                LocalDateTime.now()
            ));
        }
        
        // Test statistics retrieval performance
        long startTime = System.nanoTime();
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            int totalGames = UserGameManager.getTotalGamesPlayed(testUser.getId());
            int yearGames = UserGameManager.getGamesPlayed(testUser.getId(), "YEAR_GAME");
            int locationGames = UserGameManager.getGamesPlayed(testUser.getId(), "LOCATION_GAME");
            int bestYearScore = UserGameManager.getBestScore(testUser.getId(), "YEAR_GAME");
            int bestLocationScore = UserGameManager.getBestScore(testUser.getId(), "LOCATION_GAME");
            
            assertTrue(totalGames >= 0, "Total games should be non-negative");
            assertTrue(yearGames >= 0, "Year games should be non-negative");
            assertTrue(locationGames >= 0, "Location games should be non-negative");
            assertTrue(bestYearScore >= 0, "Best year score should be non-negative");
            assertTrue(bestLocationScore >= 0, "Best location score should be non-negative");
        }
        
        long endTime = System.nanoTime();
        long avgTimeMs = (endTime - startTime) / (iterations * 1_000_000);
        
        assertTrue(avgTimeMs < 15, "Average statistics retrieval time should be less than 15ms, was: " + avgTimeMs + "ms");
    }

    @Test
    @DisplayName("Concurrent user operations performance")
    void testConcurrentOperationsPerformance() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        long startTime = System.nanoTime();
        
        // Create multiple test users for concurrent operations
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    // Create a test user for this thread
                    String username = "concurrentUser_" + threadId + "_" + System.currentTimeMillis();
                    boolean created = dbManager.createUser(username, "password123");
                    if (!created) {
                        errorCount.incrementAndGet();
                        return;
                    }
                    
                    User user = dbManager.authenticateUser(username, "password123");
                    if (user == null) {
                        errorCount.incrementAndGet();
                        return;
                    }
                    
                    // Perform operations
                    for (int i = 0; i < operationsPerThread; i++) {
                        UserGameRecord record = new UserGameRecord(
                            user.getId(),
                            i % 2 == 0 ? "YEAR_GAME" : "LOCATION_GAME",
                            5 + (i % 6),
                            LocalDateTime.now()
                        );
                        UserGameManager.saveGameRecord(record);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        long endTime = System.nanoTime();
        executor.shutdown();
        
        assertTrue(completed, "All threads should complete within 30 seconds");
        assertEquals(0, errorCount.get(), "Should have no errors during concurrent operations");
        assertEquals(threadCount * operationsPerThread, successCount.get(), "All operations should succeed");
        
        long totalTimeMs = (endTime - startTime) / 1_000_000;
        long avgTimePerOp = totalTimeMs / (threadCount * operationsPerThread);
        
        assertTrue(avgTimePerOp < 50, "Average time per concurrent operation should be less than 50ms, was: " + avgTimePerOp + "ms");
    }

    @Test
    @DisplayName("Memory usage during extensive operations")
    void testMemoryUsageDuringOperations() {
        System.gc(); // Force garbage collection
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Perform many operations
        String username = "memoryTestUser_" + System.currentTimeMillis();
        dbManager.createUser(username, "password123");
        User user = dbManager.authenticateUser(username, "password123");
        
        for (int i = 0; i < 1000; i++) {
            UserGameRecord record = new UserGameRecord(
                user.getId(),
                "YEAR_GAME",
                i % 10,
                LocalDateTime.now()
            );
            UserGameManager.saveGameRecord(record);
            
            // Get statistics occasionally
            if (i % 100 == 0) {
                UserGameManager.getTotalGamesPlayed(user.getId());
                UserGameManager.getRecentGames(user.getId(), 10);
            }
        }
        
        System.gc(); // Force garbage collection
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        // Convert to MB for easier reading
        long memoryIncreaseMB = memoryIncrease / (1024 * 1024);
        
        assertTrue(memoryIncreaseMB < 50, "Memory usage should not increase by more than 50MB, increased by: " + memoryIncreaseMB + "MB");
    }

    @Test
    @DisplayName("UserManager performance under load")
    void testUserManagerPerformance() {
        // Test rapid user switching
        long startTime = System.nanoTime();
        int iterations = 1000;
        
        for (int i = 0; i < iterations; i++) {
            UserManager.setCurrentUser(testUser);
            assertTrue(UserManager.hasCurrentUser(), "Should have current user");
            
            User current = UserManager.getCurrentUser();
            assertNotNull(current, "Current user should not be null");
            assertEquals(testUser.getId(), current.getId(), "User ID should match");
            
            UserManager.clearCurrentUser();
            assertFalse(UserManager.hasCurrentUser(), "Should not have current user after clear");
        }
        
        long endTime = System.nanoTime();
        long avgTimeNs = (endTime - startTime) / iterations;
        long avgTimeMicros = avgTimeNs / 1000;
        
        assertTrue(avgTimeMicros < 100, "Average UserManager operation time should be less than 100 microseconds, was: " + avgTimeMicros + "µs");
    }

    @AfterAll
    void cleanUp() {
        UserManager.clearCurrentUser();
    }
}