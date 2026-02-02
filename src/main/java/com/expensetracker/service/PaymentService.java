package com.expensetracker.service;

import com.expensetracker.dao.*;
import com.expensetracker.dao.impl.*;
import com.expensetracker.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * PaymentService - Business Logic for Payment Operations
 * 
 * RESPONSIBILITIES:
 * - Record payment transactions
 * - Support partial payments
 * - Update settlement balances
 * 
 * LAYER: Business Logic Layer
 * DEPENDENCIES: TransactionDAO, SettlementDAO
 * 
 * ACID PROPERTIES:
 * - In DB implementation, payment recording should be atomic
 * - Transaction + Settlement update in single DB transaction
 */
public class PaymentService {

    private TransactionDAO transactionDAO;
    private SettlementDAO settlementDAO;
    private ExpenseDAO expenseDAO;

    public PaymentService() {
        this.transactionDAO = new TransactionDAOImpl();
        this.settlementDAO = new SettlementDAOImpl();
        this.expenseDAO = new ExpenseDAOImpl();
    }

    /**
     * Record a payment from one user to another
     * 
     * @param fromUser  User making payment
     * @param toUser    User receiving payment
     * @param amount    Payment amount
     * @param expenseId Optional linked expense ID (null for general payment)
     * @param note      Optional note
     * @return Created transaction
     */
    public Transaction recordPayment(String fromUser, String toUser, BigDecimal amount,
            Integer expenseId, String note) {
        // Validate: cannot pay yourself
        if (fromUser.equals(toUser)) {
            throw new IllegalArgumentException("Cannot make payment to yourself");
        }

        // Create transaction record
        Transaction transaction = new Transaction(fromUser, toUser, amount, expenseId, note);
        int transactionId = transactionDAO.insert(transaction);
        transaction.setTransactionId(transactionId);

        // Update settlement balance
        updateSettlementOnPayment(fromUser, toUser, amount, expenseId);

        return transaction;
    }

    /**
     * Record a partial payment
     */
    public Transaction recordPartialPayment(String fromUser, String toUser, BigDecimal amount,
            Integer expenseId, String note) {
        // Same as regular payment - partial is just about the amount
        return recordPayment(fromUser, toUser, amount, expenseId,
                "Partial payment: " + (note != null ? note : ""));
    }

    /**
     * Update settlement when payment is made
     * This simulates what the database trigger does
     */
    private void updateSettlementOnPayment(String fromUser, String toUser,
            BigDecimal amount, Integer expenseId) {
        // Find the group for this payment
        int groupId = 0;
        if (expenseId != null) {
            Expense expense = expenseDAO.findById(expenseId);
            if (expense != null) {
                groupId = expense.getGroupId();
            }
        }

        if (groupId == 0) {
            // Try to find any settlement between these users
            List<Settlement> settlements = settlementDAO.findByDebtor(fromUser);
            for (Settlement s : settlements) {
                if (s.getToUser().equals(toUser)) {
                    groupId = s.getGroupId();
                    break;
                }
            }
        }

        if (groupId > 0) {
            Settlement settlement = settlementDAO.findByUsers(groupId, fromUser, toUser);
            if (settlement != null) {
                BigDecimal newBalance = settlement.getNetBalance().subtract(amount);
                settlement.setNetBalance(newBalance);

                // Update status based on balance
                if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    settlement.setStatus(Settlement.SettlementStatus.SETTLED);
                    settlement.setNetBalance(BigDecimal.ZERO);
                } else {
                    settlement.setStatus(Settlement.SettlementStatus.PARTIAL);
                }

                settlement.setLastUpdated(new Timestamp(System.currentTimeMillis()));
                settlementDAO.update(settlement);
            }
        }
    }

    /**
     * Get all transactions for a user
     */
    public List<Transaction> getTransactionsByUser(String userId) {
        return transactionDAO.findByUser(userId);
    }

    /**
     * Get all transactions in a group
     */
    public List<Transaction> getTransactionsByGroup(int groupId) {
        return transactionDAO.findByGroup(groupId);
    }

    /**
     * Get transaction by ID
     */
    public Transaction getTransaction(int transactionId) {
        return transactionDAO.findById(transactionId);
    }

    /**
     * Get payments between two users
     */
    public List<Transaction> getPaymentsBetweenUsers(String fromUser, String toUser) {
        return transactionDAO.findByUsers(fromUser, toUser);
    }
}
