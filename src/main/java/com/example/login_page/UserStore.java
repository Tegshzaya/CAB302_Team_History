// Temporary in-memory storage for user accounts.
// This acts like a "fake database" while developing the app,
// until we connect a real database MySQL.

package com.example.login_page;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStore {
    private static final Map<String, String> USERS = new ConcurrentHashMap<>();
    
    // Initialize with some demo users for testing
    static {
        // Demo users for testing (username: hashed_password)
        USERS.put("demo", hashPassword("demo123"));
        USERS.put("test", hashPassword("test123"));
        USERS.put("admin", hashPassword("admin123"));
        USERS.put("user", hashPassword("password"));
        System.out.println("UserStore: Initialized with " + USERS.size() + " demo users (passwords hashed)");
    }

    public static boolean exists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERS.containsKey(username.toLowerCase().trim());
    }

    public static void save(String username, String passwordPlain) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (passwordPlain == null || passwordPlain.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String normalizedUsername = username.toLowerCase().trim();
        String hashedPassword = hashPassword(passwordPlain);
        USERS.put(normalizedUsername, hashedPassword);
        System.out.println("UserStore: New user registered: " + normalizedUsername + " (password hashed)");
    }

    public static boolean validate(String username, String passwordPlain) {
        if (username == null || username.trim().isEmpty() || passwordPlain == null) {
            return false;
        }

        String normalizedUsername = username.toLowerCase().trim();
        String storedHash = USERS.get(normalizedUsername);
        if (storedHash == null) {
            System.out.println("UserStore: Validation attempt for '" + normalizedUsername + "': FAILED (user not found)");
            return false;
        }

        String inputHash = hashPassword(passwordPlain);
        boolean isValid = storedHash.equals(inputHash);

        System.out.println("UserStore: Validation attempt for '" + normalizedUsername + "': " + (isValid ? "SUCCESS" : "FAILED"));
        return isValid;
    }

    /**
     * Hash a password using SHA-256 with salt for security
     * Note: In production, use BCrypt or Argon2 for better security
     */
    private static String hashPassword(String password) {
        try {
            // Generate a simple salt (in production, use a proper salt per user)
            String salt = "HistoricalArtGuesser2024";
            String saltedPassword = password + salt;
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("UserStore: Error hashing password: " + e.getMessage());
            // Fallback to plain text (not recommended for production)
            return password;
        }
    }
    
    public static int getUserCount() {
        return USERS.size();
    }
    
    public static void printAllUsers() {
        System.out.println("UserStore: Current users (" + USERS.size() + "):");
        for (String username : USERS.keySet()) {
            System.out.println("  - " + username);
        }
    }
}
