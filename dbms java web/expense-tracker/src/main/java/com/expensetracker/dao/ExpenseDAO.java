package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import java.util.List;

/**
 * ExpenseDAO Interface
 * 
 * DATA ACCESS LAYER:
 * - Abstracts database operations for Expense entity
 * 
 * SQL OPERATIONS:
 * - insert(): INSERT INTO expenses (group_id, paid_by, amount, description,
 * split_type) VALUES (?, ?, ?, ?, ?)
 * - update(): UPDATE expenses SET amount=?, description=?, split_type=? WHERE
 * expense_id=?
 * - findById(): SELECT * FROM expenses WHERE expense_id=?
 * - findByGroup(): SELECT * FROM expenses WHERE group_id=? ORDER BY created_at
 * DESC
 * - findByUser(): SELECT * FROM expenses WHERE paid_by=?
 */
public interface ExpenseDAO {

    int insert(Expense expense);

    boolean update(Expense expense);

    boolean delete(int expenseId);

    Expense findById(int expenseId);

    /**
     * Find all expenses for a group
     */
    List<Expense> findByGroup(int groupId);

    /**
     * Find all expenses paid by a user
     */
    List<Expense> findByUser(String userId);

    /**
     * Find all expenses in a group paid by a specific user
     */
    List<Expense> findByGroupAndUser(int groupId, String userId);
}
