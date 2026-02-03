package com.expensetracker.service;

import com.expensetracker.dao.*;
import com.expensetracker.dao.impl.*;
import com.expensetracker.model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * ExpenseService - Business Logic for Expense Operations
 * 
 * RESPONSIBILITIES:
 * - Add new expenses
 * - Calculate expense splits (equal/custom)
 * - Generate participant records
 * - Create settlement entries
 * 
 * LAYER: Business Logic Layer
 * DEPENDENCIES: ExpenseDAO, ExpenseParticipantDAO, SettlementDAO,
 * GroupMemberDAO
 */
public class ExpenseService {

    private ExpenseDAO expenseDAO;
    private ExpenseParticipantDAO participantDAO;
    private SettlementDAO settlementDAO;
    private GroupMemberDAO memberDAO;

    public ExpenseService() {
        this.expenseDAO = new ExpenseDAOImpl();
        this.participantDAO = new ExpenseParticipantDAOImpl();
        this.settlementDAO = new SettlementDAOImpl();
        this.memberDAO = new GroupMemberDAOImpl();
    }

    /**
     * Add a new expense with equal split among all group members
     * 
     * @param groupId     Group ID
     * @param paidBy      User ID who paid
     * @param amount      Total expense amount
     * @param description Expense description
     * @return Created expense
     */
    public Expense addExpenseEqualSplit(int groupId, String paidBy, BigDecimal amount,
            String description) {
        // Get all group members
        List<GroupMember> members = memberDAO.findByGroup(groupId);
        List<String> participantIds = members.stream()
                .map(GroupMember::getUserId)
                .collect(java.util.stream.Collectors.toList());

        return addExpenseEqualSplit(groupId, paidBy, amount, description, participantIds);
    }

    /**
     * Add expense with equal split among specified participants
     */
    public Expense addExpenseEqualSplit(int groupId, String paidBy, BigDecimal amount,
            String description, List<String> participantIds) {
        // Create expense
        Expense expense = new Expense(groupId, paidBy, amount, description,
                Expense.SplitType.EQUAL);
        int expenseId = expenseDAO.insert(expense);
        expense.setExpenseId(expenseId);

        // Calculate equal share
        int numParticipants = participantIds.size();
        BigDecimal share = amount.divide(BigDecimal.valueOf(numParticipants),
                2, RoundingMode.HALF_UP);

        // Create participant records and settlements
        for (String userId : participantIds) {
            // Create participant record
            ExpenseParticipant participant = new ExpenseParticipant(expenseId, userId, share);
            participantDAO.insert(participant);

            // Create/update settlement (if not the payer)
            if (!userId.equals(paidBy)) {
                updateOrCreateSettlement(groupId, userId, paidBy, share);
            }
        }

        return expense;
    }

    /**
     * Add expense with custom split amounts
     */
    public Expense addExpenseCustomSplit(int groupId, String paidBy, BigDecimal amount,
            String description, Map<String, BigDecimal> shares) {
        // Create expense
        Expense expense = new Expense(groupId, paidBy, amount, description,
                Expense.SplitType.CUSTOM);
        int expenseId = expenseDAO.insert(expense);
        expense.setExpenseId(expenseId);

        // Create participant records and settlements
        for (Map.Entry<String, BigDecimal> entry : shares.entrySet()) {
            String userId = entry.getKey();
            BigDecimal share = entry.getValue();

            // Create participant record
            ExpenseParticipant participant = new ExpenseParticipant(expenseId, userId, share);
            participantDAO.insert(participant);

            // Create/update settlement (if not the payer)
            if (!userId.equals(paidBy)) {
                updateOrCreateSettlement(groupId, userId, paidBy, share);
            }
        }

        return expense;
    }

    /**
     * Helper method to create or update settlement
     */
    private void updateOrCreateSettlement(int groupId, String fromUser, String toUser,
            BigDecimal amount) {
        Settlement existing = settlementDAO.findByUsers(groupId, fromUser, toUser);

        if (existing != null) {
            // Update existing settlement
            BigDecimal newBalance = existing.getNetBalance().add(amount);
            existing.setNetBalance(newBalance);
            existing.setStatus(Settlement.SettlementStatus.PENDING);
            settlementDAO.update(existing);
        } else {
            // Create new settlement
            Settlement settlement = new Settlement(groupId, fromUser, toUser, amount);
            settlementDAO.insert(settlement);
        }
    }

    /**
     * Get all expenses for a group
     */
    public List<Expense> getExpensesByGroup(int groupId) {
        return expenseDAO.findByGroup(groupId);
    }

    /**
     * Get expense by ID
     */
    public Expense getExpense(int expenseId) {
        return expenseDAO.findById(expenseId);
    }

    /**
     * Get participants for an expense
     */
    public List<ExpenseParticipant> getParticipants(int expenseId) {
        return participantDAO.findByExpense(expenseId);
    }

    /**
     * Calculate shares for equal split
     * 
     * @param amount           Total amount
     * @param participantCount Number of participants
     * @return Share per person (rounded to 2 decimal places)
     */
    public BigDecimal calculateEqualShare(BigDecimal amount, int participantCount) {
        return amount.divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP);
    }
}
