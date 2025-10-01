package com.learn.plan_b_302;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for ArtImage functionality
 */
public class ArtImageTest {

    @Test
    void testArtImageCreation() {
        // Test basic ArtImage creation
        ArtImage image = new ArtImage(
            "/test/path.jpg",
            "1503",
            "1503", "1495", "1510", "1520"
        );
        
        assertNotNull(image, "ArtImage should not be null");
        assertEquals("/test/path.jpg", image.getPath(), "Path should match");
        assertEquals("1503", image.getCorrectAnswer(), "Correct answer should match");
    }

    @Test
    void testGetAllOptions() {
        // Test getting all options
        ArtImage image = new ArtImage(
            "/test/path.jpg",
            "1503",
            "1503", "1495", "1510", "1520"
        );
        
        List<String> options = image.getAllOptions();
        assertNotNull(options, "Options list should not be null");
        assertEquals(4, options.size(), "Should have 4 options");
        assertTrue(options.contains("1503"), "Should contain correct answer");
        assertTrue(options.contains("1495"), "Should contain option 1");
        assertTrue(options.contains("1510"), "Should contain option 2");
        assertTrue(options.contains("1520"), "Should contain option 3");
    }

    @Test
    void testIsCorrectAnswer() {
        // Test correct answer validation
        ArtImage image = new ArtImage(
            "/test/path.jpg",
            "1503",
            "1503", "1495", "1510", "1520"
        );
        
        assertTrue(image.isCorrectAnswer("1503"), "Should recognize correct answer");
        assertFalse(image.isCorrectAnswer("1495"), "Should not recognize wrong answer");
        assertFalse(image.isCorrectAnswer("invalid"), "Should not recognize invalid answer");
    }

    @Test
    void testArtImageWithNullValues() {
        // Test handling of null values - ArtImage constructor doesn't throw exceptions for null values
        // This test verifies the constructor handles null gracefully
        ArtImage image = new ArtImage(null, "1503", "1503", "1495", "1510", "1520");
        assertNotNull(image, "ArtImage should be created even with null path");
        assertNull(image.getPath(), "Path should be null when passed null");
    }
}
