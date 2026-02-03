package com.expensetracker.dao;

import com.expensetracker.model.GroupMember;
import java.util.List;

/**
 * GroupMemberDAO Interface
 * 
 * DATA ACCESS LAYER:
 * - Manages the many-to-many relationship between users and groups
 * 
 * SQL OPERATIONS:
 * - insert(): INSERT INTO group_members (group_id, user_id) VALUES (?, ?)
 * - delete(): DELETE FROM group_members WHERE group_id=? AND user_id=?
 * - findByGroup(): SELECT * FROM group_members WHERE group_id=?
 * - findByUser(): SELECT * FROM group_members WHERE user_id=?
 */
public interface GroupMemberDAO {

    int insert(GroupMember member);

    boolean delete(int groupId, String userId);

    /**
     * Get all members of a group
     */
    List<GroupMember> findByGroup(int groupId);

    /**
     * Get all group memberships for a user
     */
    List<GroupMember> findByUser(String userId);

    /**
     * Check if user is member of group
     */
    boolean isMember(int groupId, String userId);
}
