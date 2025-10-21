package com.learn.plan_b_302;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/artgame.db";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Hash a password with a random salt using SHA-256
     */
    private String hashPassword(String password) {
        try {
            // Generate a random salt
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Add salt to the password
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt and hashed password
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            // Encode to Base64 for storage
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * Supports both new hashed passwords and legacy plain text passwords
     */
    private boolean verifyPassword(String password, String storedHash) {
        try {
            // Check if this is a legacy plain text password (short length, not Base64)
            if (storedHash.length() < 50 || !isValidBase64(storedHash)) {
                // Legacy plain text password - do simple string comparison
                return password.equals(storedHash);
            }
            
            // New hashed password - decode and verify
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Check if we have enough bytes for salt + hash
            if (combined.length < 48) { // 16 bytes salt + 32 bytes SHA-256 hash
                // Fallback to plain text comparison for malformed hashes
                return password.equals(storedHash);
            }
            
            // Extract salt (first 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);
            
            // Extract hashed password (remaining bytes)
            byte[] storedHashedPassword = new byte[combined.length - 16];
            System.arraycopy(combined, 16, storedHashedPassword, 0, storedHashedPassword.length);
            
            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Compare the hashes
            return MessageDigest.isEqual(hashedPassword, storedHashedPassword);
            
        } catch (Exception e) {
            // If anything goes wrong, fallback to plain text comparison
            System.err.println("Password verification error, falling back to plain text: " + e.getMessage());
            return password.equals(storedHash);
        }
    }
    
    /**
     * Check if a string is valid Base64
     */
    private boolean isValidBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Create users table with proper schema
            String createUsersTable = """
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            high_score INTEGER DEFAULT 0,
                            games_played INTEGER DEFAULT 0
                        )
                    """;

            // Create art_images table
            String createImagesTable = """
                        CREATE TABLE IF NOT EXISTS art_images (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            path TEXT NOT NULL UNIQUE,
                            correct_answer TEXT NOT NULL,
                            option1 TEXT NOT NULL,
                            option2 TEXT NOT NULL,
                            option3 TEXT NOT NULL,
                            option4 TEXT NOT NULL
                        )
                    """;
            
            // Create game_history table (detailed tracking)
            String createGameHistoryTable = """
                        CREATE TABLE IF NOT EXISTS game_history (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER NOT NULL,
                            game_type TEXT NOT NULL,
                            score INTEGER NOT NULL,
                            max_possible_score INTEGER NOT NULL,
                            questions_answered INTEGER NOT NULL,
                            correct_answers INTEGER NOT NULL,
                            played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            game_details TEXT,
                            FOREIGN KEY (user_id) REFERENCES users (id)
                        )
                    """;
            
            // Create simple user_games table (basic tracking)
            String createUserGamesTable = """
                        CREATE TABLE IF NOT EXISTS user_games (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER NOT NULL,
                            game_type TEXT NOT NULL,
                            score INTEGER NOT NULL,
                            date_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users (id)
                        )
                    """;

            conn.createStatement().execute(createUsersTable);
            conn.createStatement().execute(createImagesTable);
            conn.createStatement().execute(createGameHistoryTable);
            conn.createStatement().execute(createUserGamesTable);

            // ✅ Add unique index for path to handle old DBs without UNIQUE constraint
            conn.createStatement().execute(
                    "CREATE UNIQUE INDEX IF NOT EXISTS idx_art_images_path ON art_images(path)"
            );


            // Check and update existing schema if needed
            updateDatabaseSchema(conn);

            // Insert sample art data if table is empty
            insertSampleArtData(conn);

            // ✅ Log total rows so you know it actually seeded
            try (var st = conn.createStatement();
                 var rs = st.executeQuery("SELECT COUNT(*) FROM art_images")) {
                if (rs.next()) {
                    System.out.println("art_images rows = " + rs.getInt(1));
                }
            }

            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabaseSchema(Connection conn) throws SQLException {
        // Check if high_score column exists, if not add it
        try {
            conn.createStatement().executeQuery("SELECT high_score FROM users LIMIT 1");
        } catch (SQLException e) {
            // Column doesn't exist, add it
            conn.createStatement().execute("ALTER TABLE users ADD COLUMN high_score INTEGER DEFAULT 0");
            System.out.println("Added high_score column");
        }

        // Check if games_played column exists, if not add it
        try {
            conn.createStatement().executeQuery("SELECT games_played FROM users LIMIT 1");
        } catch (SQLException e) {
            // Column doesn't exist, add it
            conn.createStatement().execute("ALTER TABLE users ADD COLUMN games_played INTEGER DEFAULT 0");
            System.out.println("Added games_played column");
        }
    }

    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Hash the password with salt
            String hashedPassword = hashPassword(password);
            
            // Debug: Show the encryption in action
            System.out.println("🔐 ENCRYPTION DEBUG:");
            System.out.println("Original password: " + password);
            System.out.println("Encrypted password: " + hashedPassword);
            System.out.println("Encrypted length: " + hashedPassword.length());
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();

            System.out.println("✅ User created: " + username);
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error creating user: " + e.getMessage());
            return false;
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                
                // Verify the password using our custom hashing
                if (verifyPassword(password, storedHashedPassword)) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getInt("high_score"),
                            rs.getInt("games_played")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateHighScore(int userId, int score) {
        String sql = "UPDATE users SET high_score = ? WHERE id = ? AND high_score < ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, score);
            stmt.setInt(2, userId);
            stmt.setInt(3, score);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // New methods needed by GameController
    public static User refreshUser(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("high_score"),
                        rs.getInt("games_played")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static java.util.List<ArtImage> getAllImages() {
        List<ArtImage> images = new ArrayList<>();
        String sql = "SELECT * FROM art_images"; // no ORDER BY RANDOM()

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ArtImage image = new ArtImage(
                        rs.getString("path"),
                        rs.getString("correct_answer"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4")
                );
                images.add(image);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return images;
    }

    public static void updateUserScore(int userId, int score, int totalQuestions) {
        String sql = "UPDATE users SET high_score = CASE WHEN ? > high_score THEN ? ELSE high_score END, games_played = games_played + 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, score);
            stmt.setInt(2, score);
            stmt.setInt(3, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSampleArtData(Connection conn) throws SQLException {
        // Insert sample data
        String insertSql = "INSERT OR IGNORE INTO art_images " +
                "(path, correct_answer, option1, option2, option3, option4) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            Object[][] sampleData = {
                    // Format: {image_path, correct_year, option1_year, option2_year, option3_year, option4_year}
                    {"/images/mona_lisa.jpg", "1503", "1503", "1495", "1510", "1520"},
                    {"/images/starry_night.jpg", "1889", "1889", "1885", "1892", "1900"},
                    {"/images/scream.jpg", "1893", "1893", "1888", "1895", "1900"},
                    {"/images/girl_pearl.jpg", "1665", "1665", "1650", "1675", "1680"},
                    {"/images/american_gothic.jpg", "1930", "1930", "1925", "1935", "1940"},

                    // Historical battle paintings with their actual years
                    {"/images/Battle_of_Borodino.png", "1812", "1812", "1805", "1815", "1820"},
                    {"/images/Battle_of_Trafalgar.jpeg", "1805", "1805", "1800", "1810", "1815"},
                    {"/images/BattleOfWaterloo.jpeg", "1815", "1815", "1810", "1820", "1825"},
                    {"/images/Death_of_Marat_by_David.jpg", "1793", "1793", "1789", "1795", "1800"},
                    {"/images/Declaration_of_Independence.jpg", "1776", "1776", "1770", "1780", "1785"},
                    {"/images/Eugène_Ferdinand.jpg", "1832", "1832", "1825", "1840", "1845"},
                    {"/images/JEAN_LOUIS.jpg", "1794", "1794", "1790", "1800", "1805"},
                    {"/images/TheBattleOfGrunwald.jpg", "1410", "1410", "1400", "1420", "1430"},
                    {"/images/TheBattleOfSanRomano.jpeg", "1432", "1432", "1425", "1440", "1450"},
                    {"/images/TheDeathOfGeneralWarren.jpeg", "1775", "1775", "1770", "1780", "1785"},
                    {"/images/TheDeathOfGeneralWolfe.jpg", "1759", "1759", "1755", "1765", "1770"},
                    {"/images/TheDeathOfSocrates.jpeg", "1787", "1787", "1780", "1790", "1795"},
                    {"/images/TheExectionOfEmperorMaximillian.jpeg", "1867", "1867", "1860", "1870", "1875"},
                    {"/images/TheExecutionOfLadyJaneGrey.jpg", "1554", "1554", "1550", "1560", "1565"},
                    {"/images/TheMassacreatChios.jpg", "1824", "1824", "1820", "1830", "1835"},
                    {"/images/TheNightAttackOnSanLorenzo.jpg", "1432", "1432", "1425", "1440", "1450"},
                    {"/images/TheSchoolOfAthens.jpeg", "1511", "1511", "1505", "1515", "1520"},
                    {"/images/TheSiegeOfConstantinople.jpg", "1453", "1453", "1440", "1460", "1470"},
                    {"/images/Velazquez-The_Surrender_of_Breda.jpg", "1635", "1635", "1630", "1640", "1645"}
            };

            for (Object[] data : sampleData) {
                for (int i = 0; i < data.length; i++) {
                    stmt.setString(i + 1, (String) data[i]);
                }
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();

            // Optional: log how many inserts were new vs ignored
            System.out.println("Seeding complete. Inserted/ignored rows: " + java.util.Arrays.toString(results));
        }


    }
}