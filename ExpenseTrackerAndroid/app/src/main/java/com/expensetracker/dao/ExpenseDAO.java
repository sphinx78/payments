package com.expensetracker.dao;

import com.expensetracker.model.*;
import java.util.List;

/**
 * Expense Data Access Object Interface
 */
public interface ExpenseDAO {
    int insert(Expense expense);

    Expense findById(int expenseId);

    List<Expense> findByGroup(int groupId);

    List<Expense> findByPayer(String payerId);

    void update(Expense expense);

    void delete(int expenseId);
}
