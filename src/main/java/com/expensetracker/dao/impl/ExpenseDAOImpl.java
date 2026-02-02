package com.expensetracker.dao.impl;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of ExpenseDAO
 */
public class ExpenseDAOImpl implements ExpenseDAO {

    @Override
    public int insert(Expense expense) {
        String sql = "INSERT INTO expenses (group_id, paid_by, amount, description, split_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, expense.getGroupId());
            pstmt.setString(2, expense.getPaidBy());
            pstmt.setBigDecimal(3, expense.getAmount());
            pstmt.setString(4, expense.getDescription());
            pstmt.setString(5, expense.getSplitType().name());

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
    public boolean update(Expense expense) {
        String sql = "UPDATE expenses SET amount = ?, description = ?, split_type = ? WHERE expense_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, expense.getAmount());
            pstmt.setString(2, expense.getDescription());
            pstmt.setString(3, expense.getSplitType().name());
            pstmt.setInt(4, expense.getExpenseId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int expenseId) {
        String sql = "DELETE FROM expenses WHERE expense_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expenseId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Expense findById(int expenseId) {
        String sql = "SELECT * FROM expenses WHERE expense_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expenseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToExpense(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Expense> findByGroup(int groupId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE group_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRowToExpense(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    @Override
    public List<Expense> findByUser(String userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE paid_by = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRowToExpense(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    @Override
    public List<Expense> findByGroupAndUser(int groupId, String userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE group_id = ? AND paid_by = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRowToExpense(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    private Expense mapRowToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setExpenseId(rs.getInt("expense_id"));
        expense.setGroupId(rs.getInt("group_id"));
        expense.setPaidBy(rs.getString("paid_by"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setDescription(rs.getString("description"));
        expense.setSplitType(Expense.SplitType.valueOf(rs.getString("split_type")));
        expense.setCreatedAt(rs.getTimestamp("created_at"));
        return expense;
    }
}
