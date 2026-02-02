package com.expensetracker.dao;

import com.expensetracker.model.User;
import java.util.List;

/**
 * UserDAO Interface
 * 
 * DATA ACCESS LAYER:
 * - Abstracts database operations for User entity
 * - Implementations can use JDBC, JPA, or in-memory storage
 * - Controllers and services depend on this interface, not implementation
 * 
 * SQL OPERATIONS (for JDBC implementation):
 * - insert(): INSERT INTO users (name, email, phone) VALUES (?, ?, ?)
 * - update(): UPDATE users SET name=?, email=?, phone=? WHERE user_id=?
 * - delete(): DELETE FROM users WHERE user_id=?
 * - findById(): SELECT * FROM users WHERE user_id=?
 * - findByEmail(): SELECT * FROM users WHERE email=?
 * - findAll(): SELECT * FROM users
 */
public interface UserDAO {

    /**
     * Insert a new user into the database
     * 
     * @param user User object to insert
     * @return Generated user ID (Phone Number)
     */
    String insert(User user);

    /**
     * Update an existing user
     * 
     * @param user User object with updated values
     * @return true if update successful
     */
    boolean update(User user);

    /**
     * Delete a user by Phone Number
     * 
     * @param phoneNumber User Phone Number to delete
     * @return true if deletion successful
     */
    boolean delete(String phoneNumber);

    /**
     * Find a user by their Phone Number
     * 
     * @param phoneNumber User Phone Number to search
     * @return User object or null if not found
     */
    User findByPhone(String phoneNumber);

    /**
     * Find a user by their email address
     * 
     * @param email Email to search
     * @return User object or null if not found
     */
    User findByEmail(String email);

    /**
     * Get all users
     * 
     * @return List of all users
     */
    List<User> findAll();
}
