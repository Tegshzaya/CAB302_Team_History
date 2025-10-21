package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Utility tests for common operations
 */
class UtilityTest {

    @Test
    @DisplayName("Should validate string utility operations")
    void testStringUtilityOperations() {
        // Test string concatenation
        String result = "Hello" + " " + "World";
        assertEquals("Hello World", result, "String concatenation should work");
        
        // Test string replacement
        String original = "Hello World";
        String replaced = original.replace("World", "Java");
        assertEquals("Hello Java", replaced, "String replacement should work");
        
        // Test string splitting
        String[] parts = "a,b,c".split(",");
        assertEquals(3, parts.length, "String split should create 3 parts");
        assertEquals("a", parts[0], "First part should be 'a'");
        assertEquals("b", parts[1], "Second part should be 'b'");
        assertEquals("c", parts[2], "Third part should be 'c'");
    }

    @Test
    @DisplayName("Should validate number utility operations")
    void testNumberUtilityOperations() {
        // Test number parsing
        int parsedInt = Integer.parseInt("123");
        assertEquals(123, parsedInt, "Integer parsing should work");
        
        double parsedDouble = Double.parseDouble("123.45");
        assertEquals(123.45, parsedDouble, 0.01, "Double parsing should work");
        
        // Test number formatting
        String formatted = String.format("%.2f", 123.456);
        assertEquals("123.46", formatted, "Number formatting should work");
        
        // Test number conversion
        String intToString = String.valueOf(123);
        assertEquals("123", intToString, "Integer to string conversion should work");
    }

    @Test
    @DisplayName("Should validate collection utility operations")
    void testCollectionUtilityOperations() {
        // Test list creation
        java.util.List<String> list = java.util.Arrays.asList("a", "b", "c");
        assertEquals(3, list.size(), "List should have 3 elements");
        
        // Test list sorting
        java.util.List<Integer> unsorted = java.util.Arrays.asList(3, 1, 2);
        java.util.Collections.sort(unsorted);
        assertEquals(java.util.Arrays.asList(1, 2, 3), unsorted, "List should be sorted");
        
        // Test list reversal
        java.util.List<String> original = java.util.Arrays.asList("a", "b", "c");
        java.util.Collections.reverse(original);
        assertEquals(java.util.Arrays.asList("c", "b", "a"), original, "List should be reversed");
    }

    @Test
    @DisplayName("Should validate date utility operations")
    void testDateUtilityOperations() {
        // Test current date
        java.time.LocalDate today = java.time.LocalDate.now();
        assertNotNull(today, "Today should not be null");
        
        // Test date arithmetic
        java.time.LocalDate tomorrow = today.plusDays(1);
        assertTrue(tomorrow.isAfter(today), "Tomorrow should be after today");
    }

    @Test
    @DisplayName("Should validate validation utility operations")
    void testValidationUtilityOperations() {
        // Test null validation
        String nullString = null;
        String notNullString = "test";
        
        assertNull(nullString, "Null string should be null");
        assertNotNull(notNullString, "Not null string should not be null");
    }

    @Test
    @DisplayName("Should validate search utility operations")
    void testSearchUtilityOperations() {
        // Test string search
        String text = "Hello World";
        assertTrue(text.contains("World"), "Text should contain 'World'");
    }

    @Test
    @DisplayName("Should validate array utility operations")
    void testArrayUtilityOperations() {
        // Test array creation
        int[] array = {1, 2, 3, 4, 5};
        assertEquals(5, array.length, "Array should have 5 elements");
    }

    @Test
    @DisplayName("String operations should not mutate the original string")
    void testStringImmutabilityAndCaseOps() {
        String base = "hello world";

        String upper = base.toUpperCase();
        String replaced = base.replace("world", "java");

        // Original should remain unchanged
        assertEquals("hello world", base, "Original string should remain unchanged after operations");

        // New values should reflect the transformation
        assertEquals("HELLO WORLD", upper, "toUpperCase() should return uppercase version");
        assertEquals("hello java", replaced, "replace() should return modified copy");

        // Also confirm different objects when transformation changes content
        assertNotSame(base, replaced,
                "replace() with an actual change should create a different String object");
    }

    @Test
    @DisplayName("Arrays.asList should allow set() but not add() or remove()")
    void testListImmutabilityAndMutationBehavior() {
        java.util.List<String> list = java.util.Arrays.asList("x", "y", "z");

        // set() is allowed because backing array size is fixed
        list.set(1, "Y");
        assertEquals("Y", list.get(1), "set() should update element in fixed-size list");

        // add() should throw UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            list.add("new");
        }, "add() on Arrays.asList-backed list should throw UnsupportedOperationException");

        // remove() should also throw
        assertThrows(UnsupportedOperationException.class, () -> {
            list.remove(0);
        }, "remove() on Arrays.asList-backed list should throw UnsupportedOperationException");
    }

    @Test
    @DisplayName("Rounding, casting, and ceiling should behave as expected")
    void testRoundingAndCastingBehaviors() {
        double value = 3.6;

        long rounded = Math.round(value);   // nearest long
        int casted = (int) value;           // truncates toward zero
        double ceiled = Math.ceil(value);   // rounds up

        assertEquals(4L, rounded,
                "Math.round(3.6) should round to 4");
        assertEquals(3, casted,
                "Casting 3.6 to int should truncate to 3");
        assertEquals(4.0, ceiled,
                "Math.ceil(3.6) should return 4.0");
    }

    @Test
    @DisplayName("DateTime should format and parse consistently")
    void testDateFormattingAndParsingRoundTrip() {
        java.time.LocalDateTime dt = java.time.LocalDateTime.of(2025, 10, 21, 15, 30);

        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String asText = dt.format(fmt);
        assertEquals("2025-10-21 15:30", asText,
                "Formatted datetime should match expected pattern");

        java.time.LocalDateTime parsed =
                java.time.LocalDateTime.parse(asText, fmt);

        assertEquals(dt, parsed,
                "Parsed datetime should equal the original datetime");
    }
}
