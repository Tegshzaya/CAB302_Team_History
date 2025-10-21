package com.learn.plan_b_302;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages game history database operations
 */
public class GameHistoryManager {
    private static final String DB_URL = "jdbc:sqlite:artgame.db";
    
    /**
     * Save a completed game to the history
     */
    public static void saveGameHistory(GameHistory gameHistory) {
        String sql = """
            INSERT INTO game_history (user_id, game_type, score, max_possible_score, 
                                    questions_answered, correct_answers, played_at, game_details)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameHistory.getUserId());
            stmt.setString(2, gameHistory.getGameType());
            stmt.setInt(3, gameHistory.getScore());
            stmt.setInt(4, gameHistory.getMaxPossibleScore());
            stmt.setInt(5, gameHistory.getQuestionsAnswered());
            stmt.setInt(6, gameHistory.getCorrectAnswers());
            stmt.setTimestamp(7, Timestamp.valueOf(gameHistory.getPlayedAt()));
            stmt.setString(8, gameHistory.getGameDetails());
            
            stmt.executeUpdate();
            System.out.println("Game history saved: " + gameHistory.getGameType() + " - Score: " + gameHistory.getScore());
            
        } catch (SQLException e) {
            System.err.println("Error saving game history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all game history for a specific user, ordered by most recent first
     */
    public static List<GameHistory> getUserGameHistory(int userId) {
        return getUserGameHistory(userId, null, 0);
    }
    
    /**
     * Get game history for a user with optional filtering
     * @param userId User ID
     * @param gameType Filter by game type (null for all types)
     * @param limit Maximum number of records (0 for no limit)
     */
    public static List<GameHistory> getUserGameHistory(int userId, String gameType, int limit) {
        List<GameHistory> history = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT * FROM game_history WHERE user_id = ?");
        
        if (gameType != null && !gameType.isEmpty()) {
            sql.append(" AND game_type = ?");
        }
        
        sql.append(" ORDER BY played_at DESC");
        
        if (limit > 0) {
            sql.append(" LIMIT ?");
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            
            if (gameType != null && !gameType.isEmpty()) {
                stmt.setString(paramIndex++, gameType);
            }
            
            if (limit > 0) {
                stmt.setInt(paramIndex, limit);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GameHistory game = new GameHistory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("game_type"),
                        rs.getInt("score"),
                        rs.getInt("max_possible_score"),
                        rs.getInt("questions_answered"),
                        rs.getInt("correct_answers"),
                        rs.getTimestamp("played_at").toLocalDateTime(),
                        rs.getString("game_details")
                    );
                    history.add(game);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving game history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return history;
    }
    
    /**
     * Get game statistics for a user
     */
    public static GameStatistics getUserStatistics(int userId) {
        String sql = """
            SELECT 
                game_type,
                COUNT(*) as games_played,
                AVG(score) as avg_score,
                MAX(score) as best_score,
                AVG(CAST(score AS REAL) / max_possible_score * 100) as avg_percentage,
                SUM(correct_answers) as total_correct,
                SUM(questions_answered) as total_questions
            FROM game_history 
            WHERE user_id = ? 
            GROUP BY game_type
            """;
        
        GameStatistics stats = new GameStatistics();
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String gameType = rs.getString("game_type");
                    int gamesPlayed = rs.getInt("games_played");
                    double avgScore = rs.getDouble("avg_score");
                    int bestScore = rs.getInt("best_score");
                    double avgPercentage = rs.getDouble("avg_percentage");
                    int totalCorrect = rs.getInt("total_correct");
                    int totalQuestions = rs.getInt("total_questions");
                    
                    if ("YEAR_GAME".equals(gameType)) {
                        stats.yearGamesPlayed = gamesPlayed;
                        stats.yearAverageScore = avgScore;
                        stats.yearBestScore = bestScore;
                        stats.yearAveragePercentage = avgPercentage;
                        stats.yearTotalCorrect = totalCorrect;
                        stats.yearTotalQuestions = totalQuestions;
                    } else if ("LOCATION_GAME".equals(gameType)) {
                        stats.locationGamesPlayed = gamesPlayed;
                        stats.locationAverageScore = avgScore;
                        stats.locationBestScore = bestScore;
                        stats.locationAveragePercentage = avgPercentage;
                        stats.locationTotalCorrect = totalCorrect;
                        stats.locationTotalQuestions = totalQuestions;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving game statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Delete a specific game from history
     */
    public static boolean deleteGameHistory(int gameId, int userId) {
        String sql = "DELETE FROM game_history WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting game history: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get the total number of games played by a user
     */
    public static int getTotalGamesPlayed(int userId) {
        String sql = "SELECT COUNT(*) FROM game_history WHERE user_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total games played: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Inner class to hold aggregated game statistics
     */
    public static class GameStatistics {
        public int yearGamesPlayed = 0;
        public double yearAverageScore = 0.0;
        public int yearBestScore = 0;
        public double yearAveragePercentage = 0.0;
        public int yearTotalCorrect = 0;
        public int yearTotalQuestions = 0;
        
        public int locationGamesPlayed = 0;
        public double locationAverageScore = 0.0;
        public int locationBestScore = 0;
        public double locationAveragePercentage = 0.0;
        public int locationTotalCorrect = 0;
        public int locationTotalQuestions = 0;
        
        public int getTotalGamesPlayed() {
            return yearGamesPlayed + locationGamesPlayed;
        }
        
        public double getOverallAccuracy() {
            int totalQuestions = yearTotalQuestions + locationTotalQuestions;
            int totalCorrect = yearTotalCorrect + locationTotalCorrect;
            return totalQuestions == 0 ? 0.0 : (double) totalCorrect / totalQuestions * 100.0;
        }
    }
}