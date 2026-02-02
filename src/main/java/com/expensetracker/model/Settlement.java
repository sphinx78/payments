package com.expensetracker.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Settlement Model Class
 * 
 * DATABASE MAPPING:
 * - Table: settlements
 * - Primary Key: id (AUTO_INCREMENT)
 * - Foreign Keys:
 * - group_id -> groups(group_id)
 * - from_user -> users(user_id)
 * - to_user -> users(user_id)
 * - Unique Constraint: (group_id, from_user, to_user)
 * 
 * PURPOSE:
 * - Tracks NET balance between two users within a group
 * - Updated automatically via TRIGGERS when expenses/payments are recorded
 * - Used for "who owes whom" summary display
 * 
 * NORMALIZATION (3NF):
 * - Derived data is acceptable here for performance (denormalization for
 * reporting)
 * - Alternative: Compute on-the-fly from expenses and transactions
 * 
 * TRIGGER INTEGRATION:
 * - trg_after_expense_participant_insert: Creates/updates settlements
 * - trg_after_transaction_insert: Reduces balance on payment
 */
public class Settlement {

    // PRIMARY KEY - Maps to id column
    private int id;

    // FOREIGN KEY - Which group this settlement belongs to
    private int groupId;

    // FOREIGN KEY - User who owes money (debtor)
    private String fromUser;

    // FOREIGN KEY - User who is owed money (creditor)
    private String toUser;

    // Current outstanding balance
    private BigDecimal netBalance;

    // Settlement status
    private SettlementStatus status;

    // Last update timestamp
    private Timestamp lastUpdated;

    /**
     * Settlement status enumeration
     * Maps to ENUM('PENDING', 'PARTIAL', 'SETTLED') in database
     */
    public enum SettlementStatus {
        PENDING, // No payments made yet
        PARTIAL, // Some payments made, balance remaining
        SETTLED // Fully paid, balance is zero
    }

    // Default constructor
    public Settlement() {
    }

    // Parameterized constructor
    public Settlement(int id, int groupId, String fromUser, String toUser,
            BigDecimal netBalance, SettlementStatus status) {
        this.id = id;
        this.groupId = groupId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.netBalance = netBalance;
        this.status = status;
    }

    // Constructor without ID (for new settlement)
    public Settlement(int groupId, String fromUser, String toUser, BigDecimal netBalance) {
        this.groupId = groupId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.netBalance = netBalance;
        this.status = SettlementStatus.PENDING;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public SettlementStatus getStatus() {
        return status;
    }

    public void setStatus(SettlementStatus status) {
        this.status = status;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Settlement{" +
                "groupId=" + groupId +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", netBalance=" + netBalance +
                ", status=" + status +
                '}';
    }
}
