package com.expensetracker.dao;

import com.expensetracker.model.*;
import java.util.List;

/**
 * Settlement Data Access Object Interface
 */
public interface SettlementDAO {
    int insert(Settlement settlement);

    Settlement findById(int id);

    Settlement findByUsers(int groupId, String fromUser, String toUser);

    List<Settlement> findByGroup(int groupId);

    List<Settlement> findPendingByGroup(int groupId);

    List<Settlement> findSettledByGroup(int groupId);

    List<Settlement> findByDebtor(String debtorId);

    List<Settlement> findByCreditor(String creditorId);

    void update(Settlement settlement);

    void delete(int id);
}
