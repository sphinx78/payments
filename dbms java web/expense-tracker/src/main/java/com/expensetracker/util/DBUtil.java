package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility
 *
 * Uses settings from db.properties (or environment variables) so the app
 * works on any computer without editing source code.
 */
public class DBUtil {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Error loading MySQL Driver", e);
        }
    }

    /**
     * Get a connection to the database using config from db.properties or env.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword());
    }
}
