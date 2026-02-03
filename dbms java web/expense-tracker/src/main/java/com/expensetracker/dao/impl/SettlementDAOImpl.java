package com.expensetracker.dao.impl;

import com.expensetracker.dao.SettlementDAO;
import com.expensetracker.model.Settlement;
import com.expensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of SettlementDAO
 */
public class SettlementDAOImpl implements SettlementDAO {

    @Override
    public int insert(Settlement settlement) {
        String sql = "INSERT INTO settlements (group_id, from_user, to_user, net_balance, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, settlement.getGroupId());
            pstmt.setString(2, settlement.getFromUser());
            pstmt.setString(3, settlement.getToUser());
            pstmt.setBigDecimal(4, settlement.getNetBalance());
            pstmt.setString(5, settlement.getStatus().name());

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
    public boolean update(Settlement settlement) {
        String sql = "UPDATE settlements SET net_balance = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, settlement.getNetBalance());
            pstmt.setString(2, settlement.getStatus().name());
            pstmt.setInt(3, settlement.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Settlement findById(int id) {
        String sql = "SELECT * FROM settlements WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSettlement(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Settlement findByUsers(int groupId, String fromUser, String toUser) {
        String sql = "SELECT * FROM settlements WHERE group_id = ? AND from_user = ? AND to_user = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setString(2, fromUser);
            pstmt.setString(3, toUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSettlement(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Settlement> findByGroup(int groupId) {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT * FROM settlements WHERE group_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapRowToSettlement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settlements;
    }

    @Override
    public List<Settlement> findPending() {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT * FROM settlements WHERE status IN ('PENDING', 'PARTIAL')";
        try (Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                settlements.add(mapRowToSettlement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settlements;
    }

    @Override
    public List<Settlement> findPendingByGroup(int groupId) {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT * FROM settlements WHERE group_id = ? AND status IN ('PENDING', 'PARTIAL')";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapRowToSettlement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settlements;
    }

    @Override
    public List<Settlement> findSettledByGroup(int groupId) {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT * FROM settlements WHERE group_id = ? AND status = 'SETTLED'";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapRowToSettlement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settlements;
    }

    @Override
    public List<Settlement> findByDebtor(String userId) {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT * FROM settlements WHERE from_user = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapRowToSettlement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settlements;
    }

    @Override
    public List<Settlement> findByCreditor(String userId) {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT * FROM settlements WHERE to_user = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapRowToSettlement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settlements;
    }

    private Settlement mapRowToSettlement(ResultSet rs) throws SQLException {
        Settlement settlement = new Settlement();
        settlement.setId(rs.getInt("id"));
        settlement.setGroupId(rs.getInt("group_id"));
        settlement.setFromUser(rs.getString("from_user"));
        settlement.setToUser(rs.getString("to_user"));
        settlement.setNetBalance(rs.getBigDecimal("net_balance"));
        settlement.setStatus(Settlement.SettlementStatus.valueOf(rs.getString("status")));
        settlement.setLastUpdated(rs.getTimestamp("last_updated"));
        return settlement;
    }
}
