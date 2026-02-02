package com.expensetracker.dao;

import com.expensetracker.model.Transaction;
import java.util.List;

/**
 * TransactionDAO Interface
 * 
 * DATA ACCESS LAYER:
 * - Manages payment transaction records
 * 
 * SQL OPERATIONS:
 * - insert(): INSERT INTO transactions (from_user, to_user, amount, expense_id,
 * note) VALUES (?, ?, ?, ?, ?)
 * - findById(): SELECT * FROM transactions WHERE transaction_id=?
 * - findByUser(): SELECT * FROM transactions WHERE from_user=? OR to_user=?
 * - findByExpense(): SELECT * FROM transactions WHERE expense_id=?
 * 
 * ACID PROPERTIES:
 * - Insert should be wrapped in a database transaction along with settlement
 * updates
 */
public interface TransactionDAO {

    int insert(Transaction transaction);

    Transaction findById(int transactionId);

    /**
     * Find all transactions involving a user (as payer or receiver)
     */
    List<Transaction> findByUser(String userId);

    /**
     * Find all payments from one user to another
     */
    List<Transaction> findByUsers(String fromUser, String toUser);

    /**
     * Find all transactions linked to an expense
     */
    List<Transaction> findByExpense(int expenseId);

    /**
     * Find all transactions in a group
     */
    List<Transaction> findByGroup(int groupId);
}
