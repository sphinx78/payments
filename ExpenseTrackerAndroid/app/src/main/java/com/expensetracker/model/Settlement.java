package com.expensetracker.model;

import java.math.BigDecimal;

/**
 * Settlement Model Class
 * Tracks who owes whom in a group
 */
public class Settlement {
    private int id; // PRIMARY KEY
    private int groupId; // FOREIGN KEY
    private String fromUser; // FOREIGN KEY (debtor)
    private String toUser; // FOREIGN KEY (creditor)
    private BigDecimal netBalance;
    private SettlementStatus status;
    private long lastUpdated; // Unix timestamp

    /**
     * Settlement status enumeration
     */
    public enum SettlementStatus {
        PENDING,
        PARTIAL,
        SETTLED
    }

    // Constructors
    public Settlement() {
    }

    public Settlement(int groupId, String fromUser, String toUser, BigDecimal netBalance) {
        this.groupId = groupId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.netBalance = netBalance;
        this.status = SettlementStatus.PENDING;
        this.lastUpdated = System.currentTimeMillis();
    }

    public Settlement(int id, int groupId, String fromUser, String toUser,
            BigDecimal netBalance, SettlementStatus status, long lastUpdated) {
        this.id = id;
        this.groupId = groupId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.netBalance = netBalance;
        this.status = status;
        this.lastUpdated = lastUpdated;
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

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Settlement{" +
                "id=" + id +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", netBalance=" + netBalance +
                ", status=" + status +
                '}';
    }
}
