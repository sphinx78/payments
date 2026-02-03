package com.expensetracker.model;

import java.math.BigDecimal;

/**
 * Expense Model Class - Android Version
 */
public class Expense {
    private int expenseId; // PRIMARY KEY
    private int groupId; // FOREIGN KEY
    private String paidBy; // FOREIGN KEY -> User.phoneNumber
    private BigDecimal amount;
    private String description;
    private SplitType splitType;
    private long createdAt;

    /**
     * Split type enumeration
     */
    public enum SplitType {
        EQUAL,
        CUSTOM
    }

    // Constructors
    public Expense() {
    }

    public Expense(int expenseId, int groupId, String paidBy,
            BigDecimal amount, String description, SplitType splitType) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.paidBy = paidBy;
        this.amount = amount;
        this.description = description;
        this.splitType = splitType;
        this.createdAt = System.currentTimeMillis();
    }

    public Expense(int groupId, String paidBy, BigDecimal amount,
            String description, SplitType splitType) {
        this.groupId = groupId;
        this.paidBy = paidBy;
        this.amount = amount;
        this.description = description;
        this.splitType = splitType;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", paidBy=" + paidBy +
                '}';
    }
}
