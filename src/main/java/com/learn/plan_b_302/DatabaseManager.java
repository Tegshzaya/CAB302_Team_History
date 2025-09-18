package com.learn.plan_b_302;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:artgame.db";

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
                    path TEXT NOT NULL,
                    correct_answer TEXT NOT NULL,
                    option1 TEXT NOT NULL,
                    option2 TEXT NOT NULL,
                    option3 TEXT NOT NULL,
                    option4 TEXT NOT NULL
                )
            """;

            conn.createStatement().execute(createUsersTable);
            conn.createStatement().execute(createImagesTable);

            // Check and update existing schema if needed
            updateDatabaseSchema(conn);

            // Insert sample art data if table is empty
            insertSampleArtData(conn);

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

            stmt.setString(1, username);
            stmt.setString(2, password); // In production, hash the password
            stmt.executeUpdate();

            System.out.println("User created: " + username);
            return true;

        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // In production, hash and compare

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
        java.util.List<ArtImage> images = new java.util.ArrayList<>();
        String sql = "SELECT * FROM art_images ORDER BY RANDOM()";

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
        // Check if data already exists
        String checkSql = "SELECT COUNT(*) FROM art_images";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already exists
            }
        }

        // Insert sample data
        String insertSql = "INSERT INTO art_images (path, correct_answer, option1, option2, option3, option4) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            // Sample data
            Object[][] sampleData = {
                    {"/images/mona_lisa.jpg", "Mona Lisa", "Mona Lisa", "The Last Supper", "The Birth of Venus", "The Persistence of Memory"},
                    {"/images/starry_night.jpg", "The Starry Night", "The Starry Night", "Sunflowers", "The Potato Eaters", "Café Terrace at Night"},
                    {"/images/scream.jpg", "The Scream", "The Scream", "The Kiss", "Madonna", "The Dance of Life"},
                    {"/images/girl_pearl.jpg", "Girl with a Pearl Earring", "Girl with a Pearl Earring", "The Milkmaid", "View of Delft", "The Lacemaker"},
                    {"/images/american_gothic.jpg", "American Gothic", "American Gothic", "Nighthawks", "A Sunday Afternoon", "Christina's World"}
            };

            for (Object[] data : sampleData) {
                for (int i = 0; i < data.length; i++) {
                    stmt.setString(i + 1, (String) data[i]);
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}