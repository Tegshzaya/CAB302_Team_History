// Temporary in-memory storage for user accounts.
// This acts like a "fake database" while developing the app,
// until we connect a real database MySQL.

package com.example.login_page;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStore {
    private static final Map<String, String> USERS = new ConcurrentHashMap<>();

    public static boolean exists(String username) {
        return USERS.containsKey(username.toLowerCase());
    }

    public static void save(String username, String passwordPlain) {
        USERS.put(username.toLowerCase(), passwordPlain); // demo only
    }

    public static boolean validate(String username, String passwordPlain) {
        String stored = USERS.get(username.toLowerCase());
        return stored != null && stored.equals(passwordPlain);
    }
}
