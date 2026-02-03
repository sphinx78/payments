package com.expensetracker.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInspector {

    public static void main(String[] args) {
        System.out.println("=== Database Inspector ===");
        System.out.println("Connecting to database...");

        try (Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()) {

            // Inspect Users
            System.out.println("\n--- USERS ---");
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
                System.out.printf("%-15s %-20s %-25s%n", "Phone", "Name", "Email");
                System.out.println("-------------------------------------------------------------");
                boolean hasUsers = false;
                while (rs.next()) {
                    hasUsers = true;
                    System.out.printf("%-15s %-20s %-25s%n",
                            rs.getString("phone_number"),
                            rs.getString("name"),
                            rs.getString("email"));
                }
                if (!hasUsers)
                    System.out.println("(No users found)");
            }

            // Inspect Groups
            System.out.println("\n--- GROUPS ---");
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT g.group_id, g.name, g.description, u.name as creator FROM `groups` g JOIN users u ON g.created_by = u.phone_number")) {
                System.out.printf("%-5s %-25s %-30s %-20s%n", "ID", "Name", "Description", "Created By");
                System.out.println(
                        "------------------------------------------------------------------------------------");
                boolean hasGroups = false;
                while (rs.next()) {
                    hasGroups = true;
                    System.out.printf("%-5d %-25s %-30s %-20s%n",
                            rs.getInt("group_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("creator"));
                }
                if (!hasGroups)
                    System.out.println("(No groups found)");
            }

        } catch (Exception e) {
            System.err.println("Error inspecting database:");
            e.printStackTrace();
        }
        System.out.println("\n==========================");
    }
}
