package com.learn.plan_b_302;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages simple user game records - basic tracking of games played and scores
 */
public class UserGameManager {
    private static final String DB_URL = "jdbc:sqlite:artgame.db";
    
    /**
     * Save a simple game record to the database
     */
    public static void saveGameRecord(UserGameRecord record) {
        String sql = """
            INSERT INTO user_games (user_id, game_type, score, date_played)
            VALUES (?, ?, ?, ?)
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, record.getUserId());
            stmt.setString(2, record.getGameType());
            stmt.setInt(3, record.getScore());
            stmt.setTimestamp(4, Timestamp.valueOf(record.getDatePlayed()));
            
            stmt.executeUpdate();
            System.out.println("Game record saved: " + record.getGameType() + " - Score: " + record.getScore());
            
        } catch (SQLException e) {
            System.err.println("Error saving game record: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get recent games for a user (most recent first)
     */
    public static List<UserGameRecord> getRecentGames(int userId, int limit) {
        List<UserGameRecord> games = new ArrayList<>();
        System.out.println("UserGameManager.getRecentGames() called for userId=" + userId + ", limit=" + limit);
        
        String sql = """
            SELECT * FROM user_games 
            WHERE user_id = ? 
            ORDER BY date_played DESC 
            LIMIT ?
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserGameRecord game = new UserGameRecord(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("game_type"),
                        rs.getInt("score"),
                        rs.getTimestamp("date_played").toLocalDateTime()
                    );
                    games.add(game);
                    System.out.println("  Found game: " + game.getGameType() + ", score=" + game.getScore() + ", date=" + game.getDatePlayed());
                }
            }
            
            System.out.println("  Total games retrieved: " + games.size());
            
        } catch (SQLException e) {
            System.err.println("Error retrieving game records: " + e.getMessage());
            e.printStackTrace();
        }
        
        return games;
    }
    
    /**
     * Get the best score for a specific game type
     */
    public static int getBestScore(int userId, String gameType) {
        String sql = """
            SELECT MAX(score) as best_score 
            FROM user_games 
            WHERE user_id = ? AND game_type = ?
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, gameType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("best_score");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting best score: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get the total number of games played for a specific game type
     */
    public static int getGamesPlayed(int userId, String gameType) {
        String sql = """
            SELECT COUNT(*) as total_games 
            FROM user_games 
            WHERE user_id = ? AND game_type = ?
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, gameType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_games");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting games played count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get total games played across all game types
     */
    public static int getTotalGamesPlayed(int userId) {
        String sql = """
            SELECT COUNT(*) as total_games 
            FROM user_games 
            WHERE user_id = ?
            """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_games");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total games played: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Update user's current stats (placeholder method for menu refresh)
     * This method ensures fresh data is retrieved when returning to menu
     */
    public static void updateUsersCurrentStats(int userId) {
        // This method could be used to update cached statistics if needed
        // For now, it's just a placeholder to ensure menu refreshes properly
        System.out.println("Updating stats for user ID: " + userId);
    }
}