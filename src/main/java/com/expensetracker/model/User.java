package com.expensetracker.model;

import java.sql.Timestamp;

/**
 * User Model Class
 * 
 * DATABASE MAPPING:
 * - Table: users
 * - Primary Key: user_id (AUTO_INCREMENT)
 * - Unique Constraint: email
 * 
 * NORMALIZATION (3NF):
 * - All attributes depend only on the primary key (user_id)
 * - No transitive dependencies
 * - Atomic values only
 */
public class User {

    // PRIMARY KEY - Maps to phone number (String)
    private String phoneNumber;

    // Basic user information
    private String name;
    private String email; // UNIQUE constraint in database

    // Timestamp - AUTO-SET by database
    private Timestamp createdAt;

    // Default constructor (required for JDBC)
    public User() {
    }

    // Constructor
    public User(String phoneNumber, String name, String email) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
