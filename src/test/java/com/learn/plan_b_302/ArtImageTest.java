package com.learn.plan_b_302;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
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

    @Test
    void testMultipleInstancesAreIndependent() {
        // Make sure two different ArtImage instances keep their own data
        ArtImage monaLisa = new ArtImage(
                "/img/mona_lisa.jpg",
                "1503",
                "1503", "1495", "1510", "1520"
        );

        ArtImage starryNight = new ArtImage(
                "/img/starry_night.jpg",
                "1889",
                "1889", "1875", "1890", "1901"
        );

        // Paths should be different
        assertNotEquals(monaLisa.getPath(), starryNight.getPath(),
                "Each ArtImage should store its own path");

        // Correct answers should be different
        assertNotEquals(monaLisa.getCorrectAnswer(), starryNight.getCorrectAnswer(),
                "Each ArtImage should store its own correct answer");

        // Options should be different for each image
        assertNotEquals(monaLisa.getAllOptions(), starryNight.getAllOptions(),
                "Each ArtImage should keep its own set of options");

        // Cross-check correctness logic is instance-specific
        assertTrue(monaLisa.isCorrectAnswer("1503"),
                "First image should validate its own correct answer");
        assertFalse(monaLisa.isCorrectAnswer("1889"),
                "First image should not accept another image's answer");

        assertTrue(starryNight.isCorrectAnswer("1889"),
                "Second image should validate its own correct answer");
        assertFalse(starryNight.isCorrectAnswer("1503"),
                "Second image should not accept the first image's answer");
    }

    @Test
    void testIsCorrectAnswerNullInput() {
        // Passing null to isCorrectAnswer() should not throw,
        // and should return false.
        ArtImage image = new ArtImage(
                "/test/path.jpg",
                "1503",
                "1503", "1495", "1510", "1520"
        );

        assertDoesNotThrow(() -> image.isCorrectAnswer(null),
                "isCorrectAnswer(null) should not throw an exception");

        assertFalse(image.isCorrectAnswer(null),
                "Null should never be treated as a correct answer");
    }


}
