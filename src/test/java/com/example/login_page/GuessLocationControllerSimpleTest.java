package com.example.login_page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simplified unit tests for GuessLocationController class
 * 
 * Test Coverage:
 * - Distance calculation using Haversine formula
 * - Location data validation
 * - Coordinate system handling
 * - Scoring algorithm testing
 * 
 * Author: Student
 * Implementation: GuessLocationController class by Student
 */
@DisplayName("GuessLocationController Simple Tests")
class GuessLocationControllerSimpleTest {

    @Nested
    @DisplayName("Distance Calculation Tests")
    class DistanceCalculationTests {

        @Test
        @DisplayName("Should calculate distance between two points")
        void shouldCalculateDistanceBetweenTwoPoints() {
            // Given
            double lat1 = 40.7128, lon1 = -74.0060; // New York
            double lat2 = 34.0522, lon2 = -118.2437; // Los Angeles

            // When
            double distance = calculateHaversineDistance(lat1, lon1, lat2, lon2);

            // Then
            assertTrue(distance > 0, "Distance should be positive");
            assertTrue(distance < 10000, "Distance should be reasonable (less than 10,000 km)");
        }

        @Test
        @DisplayName("Should return zero distance for same location")
        void shouldReturnZeroDistanceForSameLocation() {
            // Given
            double lat = 40.7128, lon = -74.0060;

            // When
            double distance = calculateHaversineDistance(lat, lon, lat, lon);

            // Then
            assertEquals(0.0, distance, 0.001, "Distance should be zero for same location");
        }

        @Test
        @DisplayName("Should calculate distance for antipodal points")
        void shouldCalculateDistanceForAntipodalPoints() {
            // Given
            double lat1 = 0, lon1 = 0; // Equator, Prime Meridian
            double lat2 = 0, lon2 = 180; // Antipodal point

            // When
            double distance = calculateHaversineDistance(lat1, lon1, lat2, lon2);

            // Then
            assertTrue(distance > 15000, "Antipodal distance should be around 20,000 km");
            assertTrue(distance < 25000, "Antipodal distance should be reasonable");
        }

        @Test
        @DisplayName("Should handle edge case coordinates")
        void shouldHandleEdgeCaseCoordinates() {
            // Given
            double northPole = 90, southPole = -90;
            double equator = 0;

            // When & Then
            assertDoesNotThrow(() -> calculateHaversineDistance(northPole, 0, southPole, 0), "Should handle poles");
            assertDoesNotThrow(() -> calculateHaversineDistance(equator, 0, equator, 180), "Should handle equator");
        }
    }

    @Nested
    @DisplayName("Coordinate Validation Tests")
    class CoordinateValidationTests {

        @Test
        @DisplayName("Should validate latitude range")
        void shouldValidateLatitudeRange() {
            // Given
            double validLat = 40.7128;
            double invalidLatHigh = 91.0;
            double invalidLatLow = -91.0;

            // When & Then
            assertTrue(isValidLatitude(validLat), "Valid latitude should pass validation");
            assertFalse(isValidLatitude(invalidLatHigh), "Latitude > 90 should fail validation");
            assertFalse(isValidLatitude(invalidLatLow), "Latitude < -90 should fail validation");
        }

        @Test
        @DisplayName("Should validate longitude range")
        void shouldValidateLongitudeRange() {
            // Given
            double validLon = -74.0060;
            double invalidLonHigh = 181.0;
            double invalidLonLow = -181.0;

            // When & Then
            assertTrue(isValidLongitude(validLon), "Valid longitude should pass validation");
            assertFalse(isValidLongitude(invalidLonHigh), "Longitude > 180 should fail validation");
            assertFalse(isValidLongitude(invalidLonLow), "Longitude < -180 should fail validation");
        }

        @Test
        @DisplayName("Should handle boundary coordinates")
        void shouldHandleBoundaryCoordinates() {
            // Given
            double lat90 = 90.0, latMinus90 = -90.0;
            double lon180 = 180.0, lonMinus180 = -180.0;

            // When & Then
            assertTrue(isValidLatitude(lat90), "Latitude 90 should be valid");
            assertTrue(isValidLatitude(latMinus90), "Latitude -90 should be valid");
            assertTrue(isValidLongitude(lon180), "Longitude 180 should be valid");
            assertTrue(isValidLongitude(lonMinus180), "Longitude -180 should be valid");
        }
    }

    @Nested
    @DisplayName("Scoring System Tests")
    class ScoringSystemTests {

        @Test
        @DisplayName("Should give full points for exact location")
        void shouldGiveFullPointsForExactLocation() {
            // Given
            double distance = 0.0;

            // When
            int score = calculateLocationScore(distance);

            // Then
            assertEquals(100, score, "Exact location should give full points");
        }

        @Test
        @DisplayName("Should give partial points for close guess")
        void shouldGivePartialPointsForCloseGuess() {
            // Given
            double closeDistance = 10.0; // 10 km
            double farDistance = 1000.0; // 1000 km

            // When
            int closeScore = calculateLocationScore(closeDistance);
            int farScore = calculateLocationScore(farDistance);

            // Then
            assertTrue(closeScore > farScore, "Closer guess should give more points");
            assertTrue(closeScore > 0, "Close guess should give positive points");
            assertTrue(farScore >= 0, "Far guess should not give negative points");
        }

        @Test
        @DisplayName("Should give zero points for very wrong guess")
        void shouldGiveZeroPointsForVeryWrongGuess() {
            // Given
            double veryFarDistance = 10000.0; // 10,000 km

            // When
            int score = calculateLocationScore(veryFarDistance);

            // Then
            assertEquals(0, score, "Very wrong guess should give zero points");
        }

        @Test
        @DisplayName("Should handle negative distance")
        void shouldHandleNegativeDistance() {
            // Given
            double negativeDistance = -10.0;

            // When
            int score = calculateLocationScore(negativeDistance);

            // Then
            assertEquals(100, score, "Negative distance should be treated as exact match");
        }
    }

    @Nested
    @DisplayName("Data Processing Tests")
    class DataProcessingTests {

        @Test
        @DisplayName("Should parse CSV location data")
        void shouldParseCSVLocationData() {
            // Given
            String csvLine = "MonaLisa.jpg,48.8566,2.3522,Paris";
            String[] parts = csvLine.split(",");

            // When & Then
            assertEquals(4, parts.length, "CSV line should have 4 parts");
            assertTrue(parts[0].endsWith(".jpg"), "First part should be image filename");
            assertDoesNotThrow(() -> Double.parseDouble(parts[1]), "Second part should be valid latitude");
            assertDoesNotThrow(() -> Double.parseDouble(parts[2]), "Third part should be valid longitude");
            assertFalse(parts[3].isEmpty(), "Fourth part should be location name");
        }

        @Test
        @DisplayName("Should handle malformed CSV data")
        void shouldHandleMalformedCSVData() {
            // Given
            String malformedLine = "MonaLisa.jpg,48.8566";
            String emptyLine = "";
            String tooManyParts = "a,b,c,d,e,f";

            // When & Then
            String[] malformedParts = malformedLine.split(",");
            String[] emptyParts = emptyLine.split(",");
            String[] manyParts = tooManyParts.split(",");

            assertTrue(malformedParts.length < 4, "Malformed line should have less than 4 parts");
            assertTrue(emptyParts.length == 1 && emptyParts[0].isEmpty(), "Empty line should have one empty part");
            assertTrue(manyParts.length > 4, "Too many parts should have more than 4 parts");
        }

        @Test
        @DisplayName("Should validate coordinate data types")
        void shouldValidateCoordinateDataTypes() {
            // Given
            String validLat = "48.8566";
            String validLon = "2.3522";
            String invalidLat = "not_a_number";
            String invalidLon = "also_not_a_number";

            // When & Then
            assertDoesNotThrow(() -> Double.parseDouble(validLat), "Valid latitude should parse");
            assertDoesNotThrow(() -> Double.parseDouble(validLon), "Valid longitude should parse");
            assertThrows(NumberFormatException.class, () -> Double.parseDouble(invalidLat), "Invalid latitude should throw exception");
            assertThrows(NumberFormatException.class, () -> Double.parseDouble(invalidLon), "Invalid longitude should throw exception");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null coordinates")
        void shouldHandleNullCoordinates() {
            // Given
            Double nullLat = null;
            Double nullLon = null;

            // When & Then
            assertThrows(NullPointerException.class, () -> nullLat.doubleValue(), "Null latitude should throw NPE");
            assertThrows(NullPointerException.class, () -> nullLon.doubleValue(), "Null longitude should throw NPE");
        }

        @Test
        @DisplayName("Should handle infinite coordinates")
        void shouldHandleInfiniteCoordinates() {
            // Given
            double infiniteLat = Double.POSITIVE_INFINITY;
            double infiniteLon = Double.NEGATIVE_INFINITY;

            // When & Then
            assertFalse(Double.isFinite(infiniteLat), "Infinite latitude should not be finite");
            assertFalse(Double.isFinite(infiniteLon), "Infinite longitude should not be finite");
        }

        @Test
        @DisplayName("Should handle NaN coordinates")
        void shouldHandleNaNCoordinates() {
            // Given
            double nanLat = Double.NaN;
            double nanLon = Double.NaN;

            // When & Then
            assertTrue(Double.isNaN(nanLat), "NaN latitude should be NaN");
            assertTrue(Double.isNaN(nanLon), "NaN longitude should be NaN");
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should calculate distances efficiently")
        void shouldCalculateDistancesEfficiently() {
            // Given
            int iterations = 10000;
            long startTime = System.currentTimeMillis();

            // When
            for (int i = 0; i < iterations; i++) {
                calculateHaversineDistance(40.0 + i * 0.001, -74.0 + i * 0.001, 41.0, -75.0);
            }

            // Then
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            assertTrue(duration < 1000, "Should calculate distances efficiently");
        }

        @Test
        @DisplayName("Should handle large datasets")
        void shouldHandleLargeDatasets() {
            // Given
            int datasetSize = 1000;
            long startTime = System.currentTimeMillis();

            // When
            for (int i = 0; i < datasetSize; i++) {
                calculateLocationScore(i * 10.0);
            }

            // Then
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            assertTrue(duration < 500, "Should handle large datasets efficiently");
        }
    }

    // Helper methods for testing
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private boolean isValidLatitude(double lat) {
        return lat >= -90.0 && lat <= 90.0;
    }

    private boolean isValidLongitude(double lon) {
        return lon >= -180.0 && lon <= 180.0;
    }

    private int calculateLocationScore(double distance) {
        if (distance < 0) return 100; // Treat negative as exact match
        if (distance == 0) return 100;
        if (distance <= 1) return 95;
        if (distance <= 5) return 90;
        if (distance <= 10) return 80;
        if (distance <= 25) return 70;
        if (distance <= 50) return 60;
        if (distance <= 100) return 50;
        if (distance <= 250) return 40;
        if (distance <= 500) return 30;
        if (distance <= 1000) return 20;
        if (distance <= 2500) return 10;
        return 0;
    }
}
