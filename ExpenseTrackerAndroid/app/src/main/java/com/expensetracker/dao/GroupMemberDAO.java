package com.expensetracker.dao;

import com.expensetracker.model.*;
import java.util.List;

/**
 * GroupMember Data Access Object Interface
 */
public interface GroupMemberDAO {
    int insert(GroupMember member);

    GroupMember findById(int id);

    List<GroupMember> findByGroup(int groupId);

    List<GroupMember> findByUser(String userId);

    void delete(int id);
}
