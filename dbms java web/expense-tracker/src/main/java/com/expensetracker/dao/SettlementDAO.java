package com.expensetracker.dao;

import com.expensetracker.model.Settlement;
import java.util.List;

/**
 * SettlementDAO Interface
 * 
 * DATA ACCESS LAYER:
 * - Manages settlement balance records
 * 
 * SQL OPERATIONS:
 * - insert(): INSERT INTO settlements (group_id, from_user, to_user,
 * net_balance, status) VALUES (?, ?, ?, ?, ?)
 * - update(): UPDATE settlements SET net_balance=?, status=? WHERE id=?
 * - findByGroup(): SELECT * FROM settlements WHERE group_id=?
 * - findPending(): SELECT * FROM settlements WHERE status IN ('PENDING',
 * 'PARTIAL')
 * - findSettled(): SELECT * FROM settlements WHERE status='SETTLED'
 * 
 * TRIGGER INTEGRATION:
 * - In DB implementation, settlements are auto-updated by triggers
 * - In-memory implementation simulates this behavior
 */
public interface SettlementDAO {

    int insert(Settlement settlement);

    boolean update(Settlement settlement);

    Settlement findById(int id);

    /**
     * Find settlement between two users in a group
     */
    Settlement findByUsers(int groupId, String fromUser, String toUser);

    /**
     * Find all settlements for a group
     */
    List<Settlement> findByGroup(int groupId);

    /**
     * Find all pending/partial settlements
     */
    List<Settlement> findPending();

    /**
     * Find all pending settlements for a group
     */
    List<Settlement> findPendingByGroup(int groupId);

    /**
     * Find all settled settlements for a group
     */
    List<Settlement> findSettledByGroup(int groupId);

    /**
     * Find all settlements where user owes money
     */
    List<Settlement> findByDebtor(String userId);

    /**
     * Find all settlements where user is owed money
     */
    List<Settlement> findByCreditor(String userId);
}
