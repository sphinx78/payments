package com.expensetracker.model;

import java.math.BigDecimal;

/**
 * ExpenseParticipant Model Class
 * 
 * DATABASE MAPPING:
 * - Table: expense_participants
 * - Primary Key: id (AUTO_INCREMENT)
 * - Foreign Keys:
 * - expense_id -> expenses(expense_id)
 * - user_id -> users(user_id)
 * - Unique Constraint: (expense_id, user_id)
 * - Check Constraint: share_amount >= 0
 * 
 * NORMALIZATION (3NF):
 * - Separates participant data from expense (eliminates repeating groups)
 * - Each row represents ONE participant's share in ONE expense
 * - Maintains 1NF by avoiding multi-valued attributes
 */
public class ExpenseParticipant {

    // PRIMARY KEY - Maps to id column
    private int id;

    // FOREIGN KEY - References expenses(expense_id)
    private int expenseId;

    // FOREIGN KEY - References users(phone_number)
    private String userId;

    // Share amount for this participant
    private BigDecimal shareAmount; // DECIMAL(10,2) in database

    // Settlement status
    private boolean isSettled; // Has this share been paid?

    // Default constructor
    public ExpenseParticipant() {
    }

    // Parameterized constructor
    public ExpenseParticipant(int id, int expenseId, String userId,
            BigDecimal shareAmount, boolean isSettled) {
        this.id = id;
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
        this.isSettled = isSettled;
    }

    // Constructor without ID (for new participant record)
    public ExpenseParticipant(int expenseId, String userId, BigDecimal shareAmount) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
        this.isSettled = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    @Override
    public String toString() {
        return "ExpenseParticipant{" +
                "expenseId=" + expenseId +
                ", userId=" + userId +
                ", shareAmount=" + shareAmount +
                ", isSettled=" + isSettled +
                '}';
    }
}
