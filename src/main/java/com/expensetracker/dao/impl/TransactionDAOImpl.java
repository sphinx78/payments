package com.expensetracker.dao.impl;

import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Transaction;
import com.expensetracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of TransactionDAO
 */
public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public int insert(Transaction transaction) {
        String sql = "INSERT INTO transactions (from_user, to_user, amount, expense_id, note) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, transaction.getFromUser());
            pstmt.setString(2, transaction.getToUser());
            pstmt.setBigDecimal(3, transaction.getAmount());
            if (transaction.getExpenseId() != null) {
                pstmt.setInt(4, transaction.getExpenseId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, transaction.getNote());

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
    public Transaction findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Transaction> findByUser(String userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_user = ? OR to_user = ? ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByUsers(String fromUser, String toUser) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_user = ? AND to_user = ? ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fromUser);
            pstmt.setString(2, toUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByExpense(int expenseId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE expense_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expenseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByGroup(int groupId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.* FROM transactions t " +
                "JOIN expenses e ON t.expense_id = e.expense_id " +
                "WHERE e.group_id = ? " +
                "ORDER BY t.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setFromUser(rs.getString("from_user"));
        transaction.setToUser(rs.getString("to_user"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        int expenseId = rs.getInt("expense_id");
        if (!rs.wasNull()) {
            transaction.setExpenseId(expenseId);
        }
        transaction.setNote(rs.getString("note"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        return transaction;
    }
}
