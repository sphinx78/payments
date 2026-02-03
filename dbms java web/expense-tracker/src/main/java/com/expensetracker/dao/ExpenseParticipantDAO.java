package com.expensetracker.dao;

import com.expensetracker.model.ExpenseParticipant;
import java.util.List;

/**
 * ExpenseParticipantDAO Interface
 * 
 * DATA ACCESS LAYER:
 * - Manages expense participant records
 * 
 * SQL OPERATIONS:
 * - insert(): INSERT INTO expense_participants (expense_id, user_id,
 * share_amount) VALUES (?, ?, ?)
 * - update(): UPDATE expense_participants SET share_amount=?, is_settled=?
 * WHERE id=?
 * - findByExpense(): SELECT * FROM expense_participants WHERE expense_id=?
 * - findByUser(): SELECT * FROM expense_participants WHERE user_id=?
 */
public interface ExpenseParticipantDAO {

    int insert(ExpenseParticipant participant);

    boolean update(ExpenseParticipant participant);

    boolean delete(int id);

    ExpenseParticipant findById(int id);

    /**
     * Find all participants for an expense
     */
    List<ExpenseParticipant> findByExpense(int expenseId);

    /**
     * Find all expense participations for a user
     */
    List<ExpenseParticipant> findByUser(String userId);

    /**
     * Find participant record for specific expense and user
     */
    ExpenseParticipant findByExpenseAndUser(int expenseId, String userId);
}
