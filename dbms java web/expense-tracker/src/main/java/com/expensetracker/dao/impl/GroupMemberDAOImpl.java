package com.expensetracker.dao.impl;

import com.expensetracker.dao.GroupMemberDAO;
import com.expensetracker.model.GroupMember;
import com.expensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of GroupMemberDAO
 */
public class GroupMemberDAOImpl implements GroupMemberDAO {

    @Override
    public int insert(GroupMember member) {
        String sql = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, member.getGroupId());
            pstmt.setString(2, member.getUserId());

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
    public boolean delete(int groupId, String userId) {
        String sql = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setString(2, userId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<GroupMember> findByGroup(int groupId) {
        List<GroupMember> members = new ArrayList<>();
        String sql = "SELECT * FROM group_members WHERE group_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRowToGroupMember(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public List<GroupMember> findByUser(String userId) {
        List<GroupMember> members = new ArrayList<>();
        String sql = "SELECT * FROM group_members WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRowToGroupMember(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public boolean isMember(int groupId, String userId) {
        String sql = "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private GroupMember mapRowToGroupMember(ResultSet rs) throws SQLException {
        GroupMember member = new GroupMember();
        member.setId(rs.getInt("id"));
        member.setGroupId(rs.getInt("group_id"));
        member.setUserId(rs.getString("user_id"));
        member.setJoinedAt(rs.getTimestamp("joined_at"));
        return member;
    }
}
