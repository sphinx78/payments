package com.expensetracker.model;

import java.sql.Timestamp;

/**
 * Group Model Class
 * 
 * DATABASE MAPPING:
 * - Table: groups
 * - Primary Key: group_id (AUTO_INCREMENT)
 * - Foreign Key: created_by -> users(user_id)
 * 
 * NORMALIZATION (3NF):
 * - Group information stored separately from members (no multi-valued
 * attributes)
 * - created_by is a foreign key, not embedded user data
 */
public class Group {

    // PRIMARY KEY - Maps to group_id column
    private int groupId;

    // Group information
    private String name;
    private String description;

    // FOREIGN KEY - References users(phone_number)
    private String createdBy;

    // Timestamp - AUTO-SET by database
    private Timestamp createdAt;

    // Default constructor
    public Group() {
    }

    // Parameterized constructor
    public Group(int groupId, String name, String description, String createdBy) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Constructor without ID (for new group creation)
    public Group(String name, String description, String createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
