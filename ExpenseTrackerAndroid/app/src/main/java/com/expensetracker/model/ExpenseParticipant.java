package com.expensetracker.model;

import java.math.BigDecimal;

/**
 * ExpenseParticipant Model Class
 * Links users to expenses with their share amount
 */
public class ExpenseParticipant {
    private int id; // PRIMARY KEY
    private int expenseId; // FOREIGN KEY
    private String userId; // FOREIGN KEY -> User.phoneNumber
    private BigDecimal shareAmount;
    private boolean isSettled;

    // Constructors
    public ExpenseParticipant() {
    }

    public ExpenseParticipant(int expenseId, String userId, BigDecimal shareAmount) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
        this.isSettled = false;
    }

    public ExpenseParticipant(int id, int expenseId, String userId,
            BigDecimal shareAmount, boolean isSettled) {
        this.id = id;
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
        this.isSettled = isSettled;
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
                "id=" + id +
                ", expenseId=" + expenseId +
                ", userId='" + userId + '\'' +
                ", shareAmount=" + shareAmount +
                '}';
    }
}
