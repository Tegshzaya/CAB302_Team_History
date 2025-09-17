package com.example.login_page;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public static void insertUser(Connection conn, String username, String password) throws SQLException {
        String sql = "INSERT INTO UserTable ( Username, Password) VALUES ( ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }
    // Checks a user
    public static boolean checkUser(Connection conn, String username, String password) throws SQLException {
        String sql = "SELECT * FROM UserTable WHERE Username = ? AND Password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true if user exists
            }
        }
    }

}
