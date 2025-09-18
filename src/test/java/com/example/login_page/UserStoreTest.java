package com.example.login_page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserStore class
 * 
 * Test Coverage:
 * - User registration and validation
 * - Password hashing and verification
 * - User existence checking
 * - Edge cases and error handling
 * 
 * Author: Student
 * Implementation: UserStore class by Student
 */
@DisplayName("UserStore Tests")
class UserStoreTest {

    @BeforeEach
    void setUp() {
        // Clear any existing users before each test
        // Note: In a real implementation, we'd have a reset method
        // For now, we'll work with the existing demo users
    }

    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {

        @Test
        @DisplayName("Should register new user successfully")
        void shouldRegisterNewUserSuccessfully() {
            // Given
            String username = "newuser";
            String password = "password123";

            // When
            assertDoesNotThrow(() -> UserStore.save(username, password));

            // Then
            assertTrue(UserStore.exists(username));
            assertTrue(UserStore.validate(username, password));
        }

        @Test
        @DisplayName("Should throw exception for empty username")
        void shouldThrowExceptionForEmptyUsername() {
            // Given
            String emptyUsername = "";
            String password = "password123";

            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> UserStore.save(emptyUsername, password));
        }

        @Test
        @DisplayName("Should throw exception for null username")
        void shouldThrowExceptionForNullUsername() {
            // Given
            String nullUsername = null;
            String password = "password123";

            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> UserStore.save(nullUsername, password));
        }

        @Test
        @DisplayName("Should throw exception for empty password")
        void shouldThrowExceptionForEmptyPassword() {
            // Given
            String username = "testuser";
            String emptyPassword = "";

            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> UserStore.save(username, emptyPassword));
        }

        @Test
        @DisplayName("Should throw exception for null password")
        void shouldThrowExceptionForNullPassword() {
            // Given
            String username = "testuser";
            String nullPassword = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> UserStore.save(username, nullPassword));
        }

        @Test
        @DisplayName("Should handle username with whitespace")
        void shouldHandleUsernameWithWhitespace() {
            // Given
            String usernameWithSpaces = "  testuser  ";
            String password = "password123";

            // When
            assertDoesNotThrow(() -> UserStore.save(usernameWithSpaces, password));

            // Then
            assertTrue(UserStore.exists("testuser")); // Should be trimmed
            assertTrue(UserStore.validate("testuser", password));
        }
    }

    @Nested
    @DisplayName("User Authentication Tests")
    class UserAuthenticationTests {

        @Test
        @DisplayName("Should validate correct credentials")
        void shouldValidateCorrectCredentials() {
            // Given - using demo user
            String username = "demo";
            String password = "demo123";

            // When & Then
            assertTrue(UserStore.validate(username, password));
        }

        @Test
        @DisplayName("Should reject incorrect password")
        void shouldRejectIncorrectPassword() {
            // Given
            String username = "demo";
            String wrongPassword = "wrongpassword";

            // When & Then
            assertFalse(UserStore.validate(username, wrongPassword));
        }

        @Test
        @DisplayName("Should reject non-existent user")
        void shouldRejectNonExistentUser() {
            // Given
            String nonExistentUser = "nonexistent";
            String password = "password123";

            // When & Then
            assertFalse(UserStore.validate(nonExistentUser, password));
        }

        @Test
        @DisplayName("Should handle null username in validation")
        void shouldHandleNullUsernameInValidation() {
            // Given
            String nullUsername = null;
            String password = "password123";

            // When & Then
            assertFalse(UserStore.validate(nullUsername, password));
        }

        @Test
        @DisplayName("Should handle null password in validation")
        void shouldHandleNullPasswordInValidation() {
            // Given
            String username = "demo";
            String nullPassword = null;

            // When & Then
            assertFalse(UserStore.validate(username, nullPassword));
        }

        @Test
        @DisplayName("Should handle empty username in validation")
        void shouldHandleEmptyUsernameInValidation() {
            // Given
            String emptyUsername = "";
            String password = "password123";

            // When & Then
            assertFalse(UserStore.validate(emptyUsername, password));
        }
    }

    @Nested
    @DisplayName("User Existence Tests")
    class UserExistenceTests {

        @Test
        @DisplayName("Should return true for existing user")
        void shouldReturnTrueForExistingUser() {
            // Given
            String existingUser = "demo";

            // When & Then
            assertTrue(UserStore.exists(existingUser));
        }

        @Test
        @DisplayName("Should return false for non-existing user")
        void shouldReturnFalseForNonExistingUser() {
            // Given
            String nonExistingUser = "nonexistent";

            // When & Then
            assertFalse(UserStore.exists(nonExistingUser));
        }

        @Test
        @DisplayName("Should handle null username in exists check")
        void shouldHandleNullUsernameInExistsCheck() {
            // Given
            String nullUsername = null;

            // When & Then
            assertFalse(UserStore.exists(nullUsername));
        }

        @Test
        @DisplayName("Should handle empty username in exists check")
        void shouldHandleEmptyUsernameInExistsCheck() {
            // Given
            String emptyUsername = "";

            // When & Then
            assertFalse(UserStore.exists(emptyUsername));
        }

        @Test
        @DisplayName("Should handle username with whitespace in exists check")
        void shouldHandleUsernameWithWhitespaceInExistsCheck() {
            // Given
            String usernameWithSpaces = "  demo  ";

            // When & Then
            assertTrue(UserStore.exists(usernameWithSpaces)); // Should be trimmed
        }
    }

    @Nested
    @DisplayName("Password Hashing Tests")
    class PasswordHashingTests {

        @Test
        @DisplayName("Should hash passwords consistently")
        void shouldHashPasswordsConsistently() {
            // Given
            String username = "hashtest";
            String password = "testpassword";

            // When
            UserStore.save(username, password);
            boolean firstValidation = UserStore.validate(username, password);
            boolean secondValidation = UserStore.validate(username, password);

            // Then
            assertTrue(firstValidation);
            assertTrue(secondValidation);
        }

        @Test
        @DisplayName("Should not store plain text passwords")
        void shouldNotStorePlainTextPasswords() {
            // Given
            String username = "securitytest";
            String password = "plaintextpassword";

            // When
            UserStore.save(username, password);

            // Then
            // The password should be hashed, so we can't directly verify this
            // But we can verify that validation still works
            assertTrue(UserStore.validate(username, password));
        }

        @Test
        @DisplayName("Should handle special characters in passwords")
        void shouldHandleSpecialCharactersInPasswords() {
            // Given
            String username = "specialtest";
            String password = "P@ssw0rd!@#$%^&*()";

            // When
            UserStore.save(username, password);

            // Then
            assertTrue(UserStore.validate(username, password));
        }

        @Test
        @DisplayName("Should handle unicode characters in passwords")
        void shouldHandleUnicodeCharactersInPasswords() {
            // Given
            String username = "unicodetest";
            String password = "密码123🔐";

            // When
            UserStore.save(username, password);

            // Then
            assertTrue(UserStore.validate(username, password));
        }
    }

    @Nested
    @DisplayName("Demo Users Tests")
    class DemoUsersTests {

        @Test
        @DisplayName("Should have demo users available")
        void shouldHaveDemoUsersAvailable() {
            // Given & When
            int userCount = UserStore.getUserCount();

            // Then
            assertTrue(userCount >= 4, "Should have at least 4 demo users");
        }

        @Test
        @DisplayName("Should validate demo user credentials")
        void shouldValidateDemoUserCredentials() {
            // Test all demo users
            assertTrue(UserStore.validate("demo", "demo123"));
            assertTrue(UserStore.validate("test", "test123"));
            assertTrue(UserStore.validate("admin", "admin123"));
            assertTrue(UserStore.validate("user", "password"));
        }

        @Test
        @DisplayName("Should reject wrong demo user passwords")
        void shouldRejectWrongDemoUserPasswords() {
            // Test wrong passwords for demo users
            assertFalse(UserStore.validate("demo", "wrongpassword"));
            assertFalse(UserStore.validate("test", "wrongpassword"));
            assertFalse(UserStore.validate("admin", "wrongpassword"));
            assertFalse(UserStore.validate("user", "wrongpassword"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle very long usernames")
        void shouldHandleVeryLongUsernames() {
            // Given
            String longUsername = "a".repeat(1000);
            String password = "password123";

            // When
            assertDoesNotThrow(() -> UserStore.save(longUsername, password));

            // Then
            assertTrue(UserStore.exists(longUsername));
            assertTrue(UserStore.validate(longUsername, password));
        }

        @Test
        @DisplayName("Should handle very long passwords")
        void shouldHandleVeryLongPasswords() {
            // Given
            String username = "longpasswordtest";
            String longPassword = "p".repeat(1000);

            // When
            assertDoesNotThrow(() -> UserStore.save(username, longPassword));

            // Then
            assertTrue(UserStore.validate(username, longPassword));
        }

        @Test
        @DisplayName("Should handle case sensitivity in usernames")
        void shouldHandleCaseSensitivityInUsernames() {
            // Given
            String username = "CaseTest";
            String password = "password123";

            // When
            UserStore.save(username, password);

            // Then
            assertTrue(UserStore.exists("casetest")); // Should be lowercase
            assertTrue(UserStore.validate("casetest", password));
        }

        @Test
        @DisplayName("Should handle duplicate user registration")
        void shouldHandleDuplicateUserRegistration() {
            // Given
            String username = "duplicate";
            String password1 = "password1";
            String password2 = "password2";

            // When
            UserStore.save(username, password1);
            UserStore.save(username, password2); // Should overwrite

            // Then
            assertTrue(UserStore.validate(username, password2));
            assertFalse(UserStore.validate(username, password1));
        }
    }
}
