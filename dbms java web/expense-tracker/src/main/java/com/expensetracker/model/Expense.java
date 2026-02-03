package com.expensetracker.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Expense Model Class
 * 
 * DATABASE MAPPING:
 * - Table: expenses
 * - Primary Key: expense_id (AUTO_INCREMENT)
 * - Foreign Keys:
 * - group_id -> groups(group_id)
 * - paid_by -> users(user_id)
 * - Check Constraint: amount > 0
 * 
 * NORMALIZATION (3NF):
 * - Expense basic info stored here
 * - Participant shares stored separately in expense_participants (1NF - no
 * repeating groups)
 * - All attributes depend only on expense_id
 */
public class Expense {

    // PRIMARY KEY - Maps to expense_id column
    private int expenseId;

    // FOREIGN KEY - References groups(group_id)
    private int groupId;

    // FOREIGN KEY - References users(phone_number) - who paid
    private String paidBy;

    // Expense details
    private BigDecimal amount; // DECIMAL(10,2) in database
    private String description;
    private SplitType splitType; // ENUM in database

    // Timestamp - AUTO-SET by database
    private Timestamp createdAt;

    /**
     * Split type enumeration
     * Maps to ENUM('EQUAL', 'CUSTOM') in database
     */
    public enum SplitType {
        EQUAL, // Split equally among all participants
        CUSTOM // Custom amounts for each participant
    }

    // Default constructor
    public Expense() {
    }

    // Parameterized constructor
    public Expense(int expenseId, int groupId, String paidBy,
            BigDecimal amount, String description, SplitType splitType) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.paidBy = paidBy;
        this.amount = amount;
        this.description = description;
        this.splitType = splitType;
    }

    // Constructor without ID (for new expense creation)
    public Expense(int groupId, String paidBy, BigDecimal amount,
            String description, SplitType splitType) {
        this.groupId = groupId;
        this.paidBy = paidBy;
        this.amount = amount;
        this.description = description;
        this.splitType = splitType;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
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
