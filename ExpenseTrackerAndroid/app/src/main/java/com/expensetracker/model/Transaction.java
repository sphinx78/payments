package com.expensetracker.model;

import java.math.BigDecimal;

/**
 * Transaction Model Class
 * Records payment transactions between users
 */
public class Transaction {
    private int transactionId; // PRIMARY KEY
    private String fromUser; // FOREIGN KEY
    private String toUser; // FOREIGN KEY
    private BigDecimal amount;
    private Integer expenseId; // FOREIGN KEY (optional)
    private String note;
    private long createdAt; // Unix timestamp

    // Constructors
    public Transaction() {
    }

    public Transaction(String fromUser, String toUser, BigDecimal amount,
            Integer expenseId, String note) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.expenseId = expenseId;
        this.note = note;
        this.createdAt = System.currentTimeMillis();
    }

    public Transaction(int transactionId, String fromUser, String toUser,
            BigDecimal amount, Integer expenseId, String note, long createdAt) {
        this.transactionId = transactionId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.expenseId = expenseId;
        this.note = note;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", amount=" + amount +
                '}';
    }
}
