package com.expensetracker.model;

/**
 * Group Model Class - Android Version
 */
public class Group {
    private int groupId; // PRIMARY KEY (auto-increment)
    private String name;
    private String description;
    private String createdBy; // FOREIGN KEY -> User.phoneNumber
    private long createdAt; // Unix timestamp

    // Constructors
    public Group() {
    }

    public Group(int groupId, String name, String description, String createdBy) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = System.currentTimeMillis();
    }

    public Group(String name, String description, String createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = System.currentTimeMillis();
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
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
