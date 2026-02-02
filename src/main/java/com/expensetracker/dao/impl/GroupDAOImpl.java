package com.expensetracker.dao.impl;

import com.expensetracker.dao.GroupDAO;
import com.expensetracker.model.Group;
import com.expensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of GroupDAO
 */
public class GroupDAOImpl implements GroupDAO {

    @Override
    public int insert(Group group) {
        String sql = "INSERT INTO `groups` (name, description, created_by) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, group.getName());
            pstmt.setString(2, group.getDescription());
            pstmt.setString(3, group.getCreatedBy());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean update(Group group) {
        String sql = "UPDATE `groups` SET name = ?, description = ? WHERE group_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, group.getName());
            pstmt.setString(2, group.getDescription());
            pstmt.setInt(3, group.getGroupId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int groupId) {
        String sql = "DELETE FROM `groups` WHERE group_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Group findById(int groupId) {
        String sql = "SELECT * FROM `groups` WHERE group_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToGroup(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Group> findByUser(String userId) {
        // Find all groups where user is a member
        String sql = "SELECT g.* FROM `groups` g JOIN group_members gm ON g.group_id = gm.group_id WHERE gm.user_id = ?";
        List<Group> groups = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(mapRowToGroup(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public List<Group> findAll() {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM `groups`";
        try (Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                groups.add(mapRowToGroup(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    private Group mapRowToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setGroupId(rs.getInt("group_id"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));
        group.setCreatedBy(rs.getString("created_by"));
        group.setCreatedAt(rs.getTimestamp("created_at"));
        return group;
    }
}
