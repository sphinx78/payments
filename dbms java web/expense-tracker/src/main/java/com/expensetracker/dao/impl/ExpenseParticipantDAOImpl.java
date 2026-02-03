package com.expensetracker.dao.impl;

import com.expensetracker.dao.ExpenseParticipantDAO;
import com.expensetracker.model.ExpenseParticipant;
import com.expensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of ExpenseParticipantDAO
 */
public class ExpenseParticipantDAOImpl implements ExpenseParticipantDAO {

    @Override
    public int insert(ExpenseParticipant participant) {
        String sql = "INSERT INTO expense_participants (expense_id, user_id, share_amount, is_settled) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, participant.getExpenseId());
            pstmt.setString(2, participant.getUserId());
            pstmt.setBigDecimal(3, participant.getShareAmount());
            pstmt.setBoolean(4, participant.isSettled());

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
    public boolean update(ExpenseParticipant participant) {
        String sql = "UPDATE expense_participants SET share_amount = ?, is_settled = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, participant.getShareAmount());
            pstmt.setBoolean(2, participant.isSettled());
            pstmt.setInt(3, participant.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM expense_participants WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ExpenseParticipant findById(int id) {
        String sql = "SELECT * FROM expense_participants WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToParticipant(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ExpenseParticipant> findByExpense(int expenseId) {
        List<ExpenseParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM expense_participants WHERE expense_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expenseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(mapRowToParticipant(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    @Override
    public List<ExpenseParticipant> findByUser(String userId) {
        List<ExpenseParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM expense_participants WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(mapRowToParticipant(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    @Override
    public ExpenseParticipant findByExpenseAndUser(int expenseId, String userId) {
        String sql = "SELECT * FROM expense_participants WHERE expense_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expenseId);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToParticipant(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ExpenseParticipant mapRowToParticipant(ResultSet rs) throws SQLException {
        ExpenseParticipant participant = new ExpenseParticipant();
        participant.setId(rs.getInt("id"));
        participant.setExpenseId(rs.getInt("expense_id"));
        participant.setUserId(rs.getString("user_id"));
        participant.setShareAmount(rs.getBigDecimal("share_amount"));
        participant.setSettled(rs.getBoolean("is_settled"));
        return participant;
    }
}
