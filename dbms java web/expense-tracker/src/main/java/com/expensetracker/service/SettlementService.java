package com.expensetracker.service;

import com.expensetracker.dao.*;
import com.expensetracker.dao.impl.*;
import com.expensetracker.model.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * SettlementService - Business Logic for Settlement Operations
 * 
 * RESPONSIBILITIES:
 * - Compute net balances between users
 * - Generate "who owes whom" summary
 * - Optional debt simplification
 * 
 * LAYER: Business Logic Layer
 * DEPENDENCIES: SettlementDAO, UserDAO
 */
public class SettlementService {

    private SettlementDAO settlementDAO;
    private UserDAO userDAO;

    public SettlementService() {
        this.settlementDAO = new SettlementDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Get all pending settlements for a group
     */
    public List<Settlement> getPendingSettlements(int groupId) {
        return settlementDAO.findPendingByGroup(groupId);
    }

    /**
     * Get all settled settlements for a group
     */
    public List<Settlement> getSettledSettlements(int groupId) {
        return settlementDAO.findSettledByGroup(groupId);
    }

    /**
     * Get all settlements for a group
     */
    public List<Settlement> getAllSettlements(int groupId) {
        return settlementDAO.findByGroup(groupId);
    }

    /**
     * Data class for settlement summary display
     */
    /**
     * Data class for settlement summary display
     */
    public static class SettlementSummary {
        public String fromUserId;
        public String fromUserName;
        public String toUserId;
        public String toUserName;
        public BigDecimal amount;
        public String status;

        public SettlementSummary(String fromUserId, String fromUserName,
                String toUserId, String toUserName,
                BigDecimal amount, String status) {
            this.fromUserId = fromUserId;
            this.fromUserName = fromUserName;
            this.toUserId = toUserId;
            this.toUserName = toUserName;
            this.amount = amount;
            this.status = status;
        }
    }

    /**
     * Get settlement summary with user names (for display)
     */
    public List<SettlementSummary> getSettlementSummary(int groupId) {
        List<Settlement> settlements = settlementDAO.findPendingByGroup(groupId);
        List<SettlementSummary> summaries = new ArrayList<>();

        for (Settlement s : settlements) {
            if (s.getNetBalance().compareTo(BigDecimal.ZERO) > 0) {
                User fromUser = userDAO.findByPhone(s.getFromUser());
                User toUser = userDAO.findByPhone(s.getToUser());

                summaries.add(new SettlementSummary(
                        s.getFromUser(),
                        fromUser != null ? fromUser.getName() : "Unknown",
                        s.getToUser(),
                        toUser != null ? toUser.getName() : "Unknown",
                        s.getNetBalance(),
                        s.getStatus().toString()));
            }
        }

        // Sort by amount descending
        summaries.sort((a, b) -> b.amount.compareTo(a.amount));
        return summaries;
    }

    /**
     * Calculate net balance for a user in a group
     * Positive = user is owed money
     * Negative = user owes money
     */
    public BigDecimal calculateUserNetBalance(int groupId, String userId) {
        BigDecimal owes = BigDecimal.ZERO;
        BigDecimal owed = BigDecimal.ZERO;

        // Amount user owes others
        List<Settlement> asDebtor = settlementDAO.findByDebtor(userId);
        for (Settlement s : asDebtor) {
            if (s.getGroupId() == groupId &&
                    s.getStatus() != Settlement.SettlementStatus.SETTLED) {
                owes = owes.add(s.getNetBalance());
            }
        }

        // Amount others owe user
        List<Settlement> asCreditor = settlementDAO.findByCreditor(userId);
        for (Settlement s : asCreditor) {
            if (s.getGroupId() == groupId &&
                    s.getStatus() != Settlement.SettlementStatus.SETTLED) {
                owed = owed.add(s.getNetBalance());
            }
        }

        return owed.subtract(owes);
    }

    /**
     * Data class for user balance summary
     */
    public static class UserBalance {
        public String userId;
        public String userName;
        public BigDecimal netBalance; // Positive = gets back, Negative = owes

        public UserBalance(String userId, String userName, BigDecimal netBalance) {
            this.userId = userId;
            this.userName = userName;
            this.netBalance = netBalance;
        }

        public String getBalanceDescription() {
            if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
                return "gets back ₹" + netBalance;
            } else if (netBalance.compareTo(BigDecimal.ZERO) < 0) {
                return "owes ₹" + netBalance.abs();
            } else {
                return "settled up";
            }
        }
    }

    /**
     * Get balance summary for all members of a group
     */
    public List<UserBalance> getGroupBalanceSummary(int groupId) {
        List<UserBalance> balances = new ArrayList<>();
        Set<String> userIds = new HashSet<>();

        // Collect all users involved in settlements
        List<Settlement> settlements = settlementDAO.findByGroup(groupId);
        for (Settlement s : settlements) {
            userIds.add(s.getFromUser());
            userIds.add(s.getToUser());
        }

        // Calculate balance for each user
        for (String userId : userIds) {
            User user = userDAO.findByPhone(userId);
            BigDecimal netBalance = calculateUserNetBalance(groupId, userId);
            balances.add(new UserBalance(userId,
                    user != null ? user.getName() : "Unknown", netBalance));
        }

        // Sort by balance
        balances.sort((a, b) -> b.netBalance.compareTo(a.netBalance));
        return balances;
    }

    /**
     * ADVANCED: Simplify debts using graph algorithm
     * Reduces the number of transactions needed to settle all debts
     * 
     * Algorithm:
     * 1. Calculate net balance for each person
     * 2. Match creditors with debtors
     * 3. Create minimal transaction set
     */
    public List<SettlementSummary> simplifyDebts(int groupId) {
        // Calculate net balance for each user
        Map<String, BigDecimal> netBalances = new HashMap<>();
        List<Settlement> settlements = settlementDAO.findPendingByGroup(groupId);

        for (Settlement s : settlements) {
            // From user owes money (negative)
            netBalances.merge(s.getFromUser(),
                    s.getNetBalance().negate(), BigDecimal::add);
            // To user is owed money (positive)
            netBalances.merge(s.getToUser(),
                    s.getNetBalance(), BigDecimal::add);
        }

        // Separate into creditors and debtors
        List<String> usersList = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<Integer> creditors = new ArrayList<>();
        List<Integer> debtors = new ArrayList<>();

        int idx = 0;
        for (Map.Entry<String, BigDecimal> entry : netBalances.entrySet()) {
            usersList.add(entry.getKey());
            amounts.add(entry.getValue());
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(idx);
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(idx);
            }
            idx++;
        }

        // Generate simplified transactions
        List<SettlementSummary> simplified = new ArrayList<>();
        int c = 0, d = 0;

        while (c < creditors.size() && d < debtors.size()) {
            int creditorIdx = creditors.get(c);
            String creditorId = usersList.get(creditorIdx);

            int debtorIdx = debtors.get(d);
            String debtorId = usersList.get(debtorIdx);

            BigDecimal credit = amounts.get(creditorIdx);
            BigDecimal debt = amounts.get(debtorIdx).abs();

            BigDecimal transfer = credit.min(debt);

            if (transfer.compareTo(BigDecimal.ZERO) > 0) {
                User debtor = userDAO.findByPhone(debtorId);
                User creditor = userDAO.findByPhone(creditorId);

                simplified.add(new SettlementSummary(
                        debtorId, debtor != null ? debtor.getName() : "Unknown",
                        creditorId, creditor != null ? creditor.getName() : "Unknown",
                        transfer, "SIMPLIFIED"));
            }

            amounts.set(creditorIdx, credit.subtract(transfer));
            amounts.set(debtorIdx, amounts.get(debtorIdx).add(transfer));

            if (amounts.get(creditorIdx).compareTo(BigDecimal.ZERO) == 0)
                c++;
            if (amounts.get(debtorIdx).compareTo(BigDecimal.ZERO) == 0)
                d++;
        }

        return simplified;
    }
}
