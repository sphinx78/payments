package com.expensetracker.model;

/**
 * GroupMember Model Class
 * Junction table for Group-User many-to-many relationship
 */
public class GroupMember {
    private int id; // PRIMARY KEY
    private int groupId; // FOREIGN KEY
    private String userId; // FOREIGN KEY -> User.phoneNumber
    private long joinedAt; // Unix timestamp

    // Constructors
    public GroupMember() {
    }

    public GroupMember(int groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = System.currentTimeMillis();
    }

    public GroupMember(int id, int groupId, String userId, long joinedAt) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = joinedAt;
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

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
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
