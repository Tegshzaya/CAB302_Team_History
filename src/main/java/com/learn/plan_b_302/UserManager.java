package com.learn.plan_b_302;

/**
 * Manages the currently logged-in user across the application
 */
public class UserManager {
    private static User currentUser = null;
    
    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("UserManager: Current user set to " + (user != null ? user.getUsername() : "null"));
    }
    
    /**
     * Get the current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Clear the current user (for logout)
     */
    public static void clearCurrentUser() {
        System.out.println("UserManager: Clearing current user");
        currentUser = null;
    }
    
    /**
     * Check if a user is currently logged in
     */
    public static boolean hasCurrentUser() {
        return currentUser != null;
    }
}