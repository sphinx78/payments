package com.expensetracker.dao;

import com.expensetracker.model.Group;
import java.util.List;

/**
 * GroupDAO Interface
 * 
 * DATA ACCESS LAYER:
 * - Abstracts database operations for Group entity
 * 
 * SQL OPERATIONS (for JDBC implementation):
 * - insert(): INSERT INTO groups (name, description, created_by) VALUES (?, ?,
 * ?)
 * - update(): UPDATE groups SET name=?, description=? WHERE group_id=?
 * - findById(): SELECT * FROM groups WHERE group_id=?
 * - findByUser(): SELECT g.* FROM groups g
 * JOIN group_members gm ON g.group_id = gm.group_id
 * WHERE gm.user_id=?
 * - findAll(): SELECT * FROM groups
 */
public interface GroupDAO {

    int insert(Group group);

    boolean update(Group group);

    boolean delete(int groupId);

    Group findById(int groupId);

    /**
     * Find all groups a user belongs to
     * 
     * @param userId User Phone Number
     * @return List of groups
     */
    List<Group> findByUser(String userId);

    List<Group> findAll();
}
