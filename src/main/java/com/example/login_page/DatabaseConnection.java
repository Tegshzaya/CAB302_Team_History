package com.example.login_page;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.IOException;


public class DatabaseConnection {
    /**
     * The singleton instance of the database connection.
     */
    private static Connection instance = null;
    /**
     * Constructor intializes the connection.
     */
    private DatabaseConnection() {
        String url = "jdbc:sqlite:database.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }
    /**
     * Provides global access to the singleton instance of the database connection.
     * @return a handle to the singleton instance of the database connection.
     */
    public static Connection getInstance() {
        if (instance == null) {
            new DatabaseConnection();
        }
        return instance;
    }
    // Adds a user
    public void insertUser(Connection conn, int userId, String username, String password) throws SQLException {
        String sql = "INSERT INTO UserTable (User_ID, Username, Password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        }
    }
}
