package com.expensetracker.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Creates the database and tables. Uses db.properties or environment variables
 * so it works on any computer without editing source code.
 */
public class DatabaseSetup {

    public static void main(String[] args) {
        try {
            System.out.println("Connecting to MySQL server...");
            try (Connection conn = DriverManager.getConnection(
                    DatabaseConfig.getServerUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword());
                    Statement stmt = conn.createStatement()) {

                // Create Database
                System.out.println("Creating database 'expense_tracker_db' if not exists...");
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS expense_tracker_db");
                System.out.println("Database created or already exists.");

                // Switch to the database
                stmt.executeUpdate("USE expense_tracker_db");

                // Read and execute schema.sql
                System.out.println("Executing schema.sql...");
                executeSqlScript(stmt, "sql/schema.sql");
                System.out.println("Schema executed successfully.");

                // Insert seed user for testing
                System.out.println("Inserting seed user...");
                stmt.executeUpdate(
                        "INSERT INTO users (phone_number, name, email) VALUES ('1234567890', 'Test User', 'test@example.com') ON DUPLICATE KEY UPDATE name=name");
                System.out.println("Seed user inserted.");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeSqlScript(Statement stmt, String filePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder sql = new StringBuilder();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                    continue;
                }

                sql.append(line).append(" ");

                if (line.endsWith(";")) {
                    String query = sql.toString().replace(";", "").trim();
                    if (!query.isEmpty()) {
                        stmt.execute(query);
                    }
                    sql.setLength(0);
                }
            }
        }
    }
}
