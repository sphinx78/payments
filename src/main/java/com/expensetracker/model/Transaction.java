package com.expensetracker.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Transaction Model Class
 * 
 * DATABASE MAPPING:
 * - Table: transactions
 * - Primary Key: transaction_id (AUTO_INCREMENT)
 * - Foreign Keys:
 * - from_user -> users(user_id)
 * - to_user -> users(user_id)
 * - expense_id -> expenses(expense_id) [NULLABLE]
 * - Check Constraints:
 * - from_user != to_user
 * - amount > 0
 * 
 * NORMALIZATION (3NF):
 * - Records individual payment transactions
 * - Separate from settlements (which track net balances)
 * - Optional link to expense allows standalone payments
 * 
 * ACID PROPERTIES:
 * - Transactions (payments) should be wrapped in DB transactions
 * - Atomicity: Payment + settlement update happen together
 * - Consistency: Balance constraints maintained
 */
public class Transaction {

    // PRIMARY KEY - Maps to transaction_id column
    private int transactionId;

    // FOREIGN KEY - User making the payment
    private String fromUser;

    // FOREIGN KEY - User receiving the payment
    private String toUser;

    // Payment amount (CHECK: must be > 0)
    private BigDecimal amount;

    // FOREIGN KEY (NULLABLE) - Optional link to specific expense
    private Integer expenseId;

    // Optional note/description
    private String note;

    // Timestamp - When payment was made
    private Timestamp createdAt;

    // Default constructor
    public Transaction() {
    }

    // Parameterized constructor
    public Transaction(int transactionId, String fromUser, String toUser,
            BigDecimal amount, Integer expenseId, String note) {
        this.transactionId = transactionId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.expenseId = expenseId;
        this.note = note;
    }

    // Constructor without ID (for new transaction)
    public Transaction(String fromUser, String toUser, BigDecimal amount,
            Integer expenseId, String note) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.expenseId = expenseId;
        this.note = note;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
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
