package com.learn.plan_b_302;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple validation tests for basic functionality
 */
class SimpleValidationTest {

    @Test
    @DisplayName("Should validate basic string operations")
    void testBasicStringOperations() {
        String testString = "Hello World";
        
        assertNotNull(testString, "String should not be null");
        assertEquals("Hello World", testString, "String should match expected value");
        assertTrue(testString.length() > 0, "String should have length greater than 0");
        assertFalse(testString.isEmpty(), "String should not be empty");
    }

    @Test
    @DisplayName("Should validate basic integer operations")
    void testBasicIntegerOperations() {
        int testInt = 42;
        
        assertEquals(42, testInt, "Integer should match expected value");
        assertTrue(testInt > 0, "Integer should be positive");
        assertFalse(testInt < 0, "Integer should not be negative");
        assertNotEquals(0, testInt, "Integer should not be zero");
    }

    @Test
    @DisplayName("Should validate basic boolean operations")
    void testBasicBooleanOperations() {
        boolean testBoolean = true;
        
        assertTrue(testBoolean, "Boolean should be true");
        assertFalse(!testBoolean, "Negated boolean should be false");
    }

    @Test
    @DisplayName("Should validate null checks")
    void testNullChecks() {
        String nullString = null;
        String notNullString = "test";
        
        assertNull(nullString, "Null string should be null");
        assertNotNull(notNullString, "Not null string should not be null");
    }

    @Test
    @DisplayName("Should validate array operations")
    void testArrayOperations() {
        String[] testArray = {"apple", "banana", "cherry"};
        
        assertNotNull(testArray, "Array should not be null");
        assertEquals(3, testArray.length, "Array should have 3 elements");
        assertEquals("apple", testArray[0], "First element should be apple");
        assertEquals("banana", testArray[1], "Second element should be banana");
        assertEquals("cherry", testArray[2], "Third element should be cherry");
    }

    @Test
    @DisplayName("Should validate list operations")
    void testListOperations() {
        java.util.List<String> testList = new java.util.ArrayList<>();
        testList.add("item1");
        testList.add("item2");
        
        assertNotNull(testList, "List should not be null");
        assertEquals(2, testList.size(), "List should have 2 elements");
        assertTrue(testList.contains("item1"), "List should contain item1");
        assertTrue(testList.contains("item2"), "List should contain item2");
        assertFalse(testList.isEmpty(), "List should not be empty");
    }

    @Test
    @DisplayName("Should validate exception handling")
    void testExceptionHandling() {
        assertDoesNotThrow(() -> {
            // This should not throw an exception
            String test = "safe operation";
            test.length();
        }, "Safe operation should not throw exception");
        
        assertThrows(ArithmeticException.class, () -> {
            // This should throw an exception
            int result = 10 / 0;
        }, "Division by zero should throw ArithmeticException");
    }

    @Test
    @DisplayName("Should validate object creation")
    void testObjectCreation() {
        Object testObject = new Object();
        
        assertNotNull(testObject, "Object should not be null");
        assertTrue(testObject instanceof Object, "Object should be instance of Object");
    }

    @Test
    @DisplayName("Should validate string manipulation")
    void testStringManipulation() {
        String original = "  Hello World  ";
        String trimmed = original.trim();
        
        assertEquals("Hello World", trimmed, "Trimmed string should remove whitespace");
    }

    @Test
    @DisplayName("Should validate mathematical operations")
    void testMathematicalOperations() {
        int a = 10;
        int b = 5;
        
        assertEquals(15, a + b, "Addition should work correctly");
        assertEquals(2, a / b, "Division should work correctly");
    }

    @Test
    @DisplayName("Should validate comparison operations")
    void testComparisonOperations() {
        int a = 10;
        int b = 5;
        
        assertTrue(a > b, "a should be greater than b");
        assertTrue(a != b, "a should not be equal to b");
    }

    @Test
    @DisplayName("Should distinguish between string reference equality and value equality")
    void testStringReferenceVsValueEquality() {
        String literalA = "test";
        String literalB = "test";
        String constructed = new String("test"); // forces a new object

        // Same interned literal usually points to the same reference
        assertTrue(literalA == literalB,
                "Two identical string literals should reference the same object (string pool)");

        // But a new String() is a different object
        assertFalse(literalA == constructed,
                "Literal and new String() should not be the same reference");

        // All should still be .equals() equal in value
        assertEquals(literalA, literalB,
                "Literals should have equal value");
        assertEquals(literalA, constructed,
                "Literal and new String() should be equal by value");
    }

    @Test
    @DisplayName("Should handle floating point math with tolerance")
    void testFloatingPointPrecision() {
        double sum = 0.1 + 0.2; // 0.30000000000000004 in binary floating point
        assertEquals(0.3, sum, 1e-9,
                "Floating point addition should be approximately equal to 0.3 within tolerance");
    }

    @Test
    @DisplayName("Should throw on array out-of-bounds access")
    void testArrayOutOfBounds() {
        int[] nums = {1, 2, 3};

        assertThrows(IndexOutOfBoundsException.class, () -> {
            int bad = nums[3]; // invalid index, length is 3 -> last index is 2
        }, "Accessing index 3 in a 3-element array should throw IndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Should validate copying a list creates independent list")
    void testListCopyIndependence() {
        java.util.List<String> original = new java.util.ArrayList<>();
        original.add("alpha");
        original.add("beta");

        // Copy using copy constructor
        java.util.List<String> copy = new java.util.ArrayList<>(original);

        // Modify the copy
        copy.add("gamma");

        // Original should remain unchanged in size and content
        assertEquals(2, original.size(),
                "Original list size should remain 2 after modifying the copy");
        assertTrue(original.contains("alpha") && original.contains("beta"),
                "Original list should still contain original elements only");

        // Copy should now have 3 elements
        assertEquals(3, copy.size(),
                "Copy list should have 3 elements after modification");
        assertTrue(copy.contains("gamma"),
                "Copy should contain the new element that was added");
    }

    @Test
    @DisplayName("Should validate boolean logic operations (AND / OR)")
    void testBooleanLogicOperators() {
        boolean a = true;
        boolean b = false;

        assertTrue(a || b, "true OR false should be true");
        assertFalse(a && b, "true AND false should be false");
        assertTrue((a && !b) || (!a && b),
                "Exclusive-like condition should be true for (true, false)");
    }

    @Test
    @DisplayName("Should handle negative number math correctly")
    void testNegativeMath() {
        int x = -5;
        int y = 2;

        assertEquals(-3, x + y, "Sum of -5 and 2 should be -3");
        assertEquals(-10, x * y, "Product of -5 and 2 should be -10");
        assertTrue(x < 0, "x should be negative");
        assertTrue(y > 0, "y should be positive");
    }
}
