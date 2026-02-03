package com.expensetracker.model;

import java.sql.Timestamp;

/**
 * GroupMember Model Class
 * 
 * DATABASE MAPPING:
 * - Table: group_members
 * - Primary Key: id (AUTO_INCREMENT)
 * - Foreign Keys:
 * - group_id -> groups(group_id)
 * - user_id -> users(user_id)
 * - Unique Constraint: (group_id, user_id) - prevents duplicate memberships
 * 
 * NORMALIZATION (3NF):
 * - This is a JUNCTION TABLE for many-to-many relationship
 * - Resolves the multi-valued dependency between users and groups
 * - No redundant data - only stores relationship identifiers
 */
public class GroupMember {

    // PRIMARY KEY - Surrogate key for the relationship
    private int id;

    // FOREIGN KEY - References groups(group_id)
    private int groupId;

    // FOREIGN KEY - References users(phone_number)
    private String userId;

    // Timestamp - When user joined the group
    private Timestamp joinedAt;

    // Default constructor
    public GroupMember() {
    }

    // Parameterized constructor
    public GroupMember(int id, int groupId, String userId) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
    }

    // Constructor without ID (for new membership)
    public GroupMember(int groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
