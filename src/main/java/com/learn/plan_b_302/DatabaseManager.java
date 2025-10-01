package com.learn.plan_b_302;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                            path TEXT NOT NULL UNIQUE,
                            correct_answer TEXT NOT NULL,
                            option1 TEXT NOT NULL,
                            option2 TEXT NOT NULL,
                            option3 TEXT NOT NULL,
                            option4 TEXT NOT NULL
                        )
                    """;

            conn.createStatement().execute(createUsersTable);
            conn.createStatement().execute(createImagesTable);

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
                    {"/images/mona_lisa.jpg", "Mona Lisa", "Mona Lisa", "The Last Supper", "The Birth of Venus", "The Persistence of Memory"},
                    {"/images/starry_night.jpg", "The Starry Night", "The Starry Night", "Sunflowers", "The Potato Eaters", "Café Terrace at Night"},
                    {"/images/scream.jpg", "The Scream", "The Scream", "The Kiss", "Madonna", "The Dance of Life"},
                    {"/images/girl_pearl.jpg", "Girl with a Pearl Earring", "Girl with a Pearl Earring", "The Milkmaid", "View of Delft", "The Lacemaker"},
                    {"/images/american_gothic.jpg", "American Gothic", "American Gothic", "Nighthawks", "A Sunday Afternoon", "Christina's World"},

                    // Your extended list
                    {"/images/Battle_of_Borodino.png", "Battle of Borodino", "Battle of Borodino", "Battle of Austerlitz", "Battle of Leipzig", "Battle of Smolensk"},
                    {"/images/Battle_of_Trafalgar.jpeg", "Battle of Trafalgar", "Battle of Trafalgar", "Battle of Waterloo", "Battle of the Nile", "Battle of Copenhagen"},
                    {"/images/BattleOfWaterloo.jpeg", "Battle of Waterloo", "Battle of Waterloo", "Battle of Borodino", "Battle of Trafalgar", "Battle of Gettysburg"},
                    {"/images/Death_of_Marat_by_David.jpg", "The Death of Marat", "The Death of Marat", "Napoleon Crossing the Alps", "Liberty Leading the People", "The Oath of the Horatii"},
                    {"/images/Declaration_of_Independence.jpg", "Declaration of Independence", "Declaration of Independence", "Washington Crossing the Delaware", "Signing of the Constitution", "Boston Tea Party"},
                    {"/images/Eugène_Ferdinand.jpg", "Portrait of Eugène Ferdinand Victor Delacroix", "Portrait of Eugène Ferdinand Victor Delacroix", "Portrait of Napoleon", "Portrait of Goethe", "Portrait of Chopin"},
                    {"/images/JEAN_LOUIS.jpg", "Portrait of Jean-Louis David", "Portrait of Jean-Louis David", "Self-Portrait of Rembrandt", "Portrait of Voltaire", "Portrait of Robespierre"},
                    {"/images/TheBattleOfGrunwald.jpg", "The Battle of Grunwald", "The Battle of Grunwald", "The Battle of Varna", "The Siege of Vienna", "The Battle of Legnica"},
                    {"/images/TheBattleOfSanRomano.jpeg", "The Battle of San Romano", "The Battle of San Romano", "The Battle of Anghiari", "The Battle of Cascina", "The Battle of Lepanto"},
                    {"/images/TheDeathOfGeneralWarren.jpeg", "The Death of General Warren", "The Death of General Warren", "The Death of General Wolfe", "Washington at Princeton", "The Battle of Bunker Hill"},
                    {"/images/TheDeathOfGeneralWolfe.jpg", "The Death of General Wolfe", "The Death of General Wolfe", "The Death of General Warren", "The Death of Nelson", "Washington Crossing the Delaware"},
                    {"/images/TheDeathOfSocrates.jpeg", "The Death of Socrates", "The Death of Socrates", "The School of Athens", "The Trial of Socrates", "The Death of Seneca"},
                    {"/images/TheExectionOfEmperorMaximillian.jpeg", "The Execution of Emperor Maximilian", "The Execution of Emperor Maximilian", "The Execution of Lady Jane Grey", "The Massacre at Chios", "Liberty Leading the People"},
                    {"/images/TheExecutionOfLadyJaneGrey.jpg", "The Execution of Lady Jane Grey", "The Execution of Lady Jane Grey", "The Execution of Emperor Maximilian", "Marie Antoinette on the Way to the Guillotine", "The Death of Marat"},
                    {"/images/TheMassacreatChios.jpg", "The Massacre at Chios", "The Massacre at Chios", "Liberty Leading the People", "The Raft of the Medusa", "Scenes from the Chios War"},
                    {"/images/TheNightAttackOnSanLorenzo.jpg", "The Night Attack on San Lorenzo", "The Night Attack on San Lorenzo", "The Battle of San Romano", "The Siege of Constantinople", "The Surrender of Breda"},
                    {"/images/TheSchoolOfAthens.jpeg", "The School of Athens", "The School of Athens", "The Last Supper", "The Creation of Adam", "Primavera"},
                    {"/images/TheSiegeOfConstantinople.jpg", "The Siege of Constantinople", "The Siege of Constantinople", "The Fall of Constantinople", "The Capture of Constantinople", "The Battle of Lepanto"},
                    {"/images/Velazquez-The_Surrender_of_Breda.jpg", "The Surrender of Breda", "The Surrender of Breda", "Las Meninas", "The Triumph of Bacchus", "The Coronation of the Virgin"}
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