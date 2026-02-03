package com.expensetracker.util;

import com.expensetracker.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory Data Store
 * 
 * PURPOSE:
 * - Provides centralized storage for demo/testing without database
 * - Simulates database tables using HashMaps
 * - Auto-increment IDs using AtomicInteger (except for Users)
 * 
 * NOTE: In production, this would be replaced with actual JDBC connections
 * to MySQL/PostgreSQL database using the same DAO interfaces.
 */
public class DataStore {

    // Singleton instance
    private static DataStore instance;

    // Data storage maps (simulate database tables)
    private Map<String, User> users = new HashMap<>(); // Key: PhoneNumber
    private Map<Integer, Group> groups = new HashMap<>();
    private Map<Integer, GroupMember> groupMembers = new HashMap<>();
    private Map<Integer, Expense> expenses = new HashMap<>();
    private Map<Integer, ExpenseParticipant> expenseParticipants = new HashMap<>();
    private Map<Integer, Transaction> transactions = new HashMap<>();
    private Map<Integer, Settlement> settlements = new HashMap<>();

    // Auto-increment counters (simulate AUTO_INCREMENT)
    // userIdCounter removed as Phone Number is PK
    private AtomicInteger groupIdCounter = new AtomicInteger(0);
    private AtomicInteger groupMemberIdCounter = new AtomicInteger(0);
    private AtomicInteger expenseIdCounter = new AtomicInteger(0);
    private AtomicInteger expenseParticipantIdCounter = new AtomicInteger(0);
    private AtomicInteger transactionIdCounter = new AtomicInteger(0);
    private AtomicInteger settlementIdCounter = new AtomicInteger(0);

    // Private constructor for singleton
    private DataStore() {
        initializeSampleData();
    }

    // Singleton accessor
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Initialize sample data for demonstration
     * Matches the data.sql sample data
     */
    private void initializeSampleData() {
        // Create users
        User rahul = new User("9876543210", "Rahul Sharma", "rahul@email.com");
        User priya = new User("9876543211", "Priya Patel", "priya@email.com");
        User amit = new User("9876543212", "Amit Kumar", "amit@email.com");
        User sneha = new User("9876543213", "Sneha Gupta", "sneha@email.com");
        User vikram = new User("9876543214", "Vikram Singh", "vikram@email.com");

        addUser(rahul);
        addUser(priya);
        addUser(amit);
        addUser(sneha);
        addUser(vikram);

        // Create groups
        Group goaTrip = new Group("Goa Trip 2024", "Friends trip to Goa in December", "9876543210");
        Group roommates = new Group("Apartment Roommates", "Monthly shared expenses", "9876543210");

        addGroup(goaTrip);
        addGroup(roommates);

        // Add members to groups
        addGroupMember(new GroupMember(1, "9876543210")); // Rahul in Goa Trip
        addGroupMember(new GroupMember(1, "9876543211")); // Priya in Goa Trip
        addGroupMember(new GroupMember(1, "9876543212")); // Amit in Goa Trip
        addGroupMember(new GroupMember(1, "9876543213")); // Sneha in Goa Trip

        addGroupMember(new GroupMember(2, "9876543210")); // Rahul in Roommates
        addGroupMember(new GroupMember(2, "9876543211")); // Priya in Roommates
        addGroupMember(new GroupMember(2, "9876543214")); // Vikram in Roommates

        // Create expenses for Goa Trip
        Expense hotel = new Expense(1, "9876543210", new BigDecimal("8000.00"),
                "Hotel booking for 2 nights", Expense.SplitType.EQUAL);
        Expense dinner = new Expense(1, "9876543211", new BigDecimal("2400.00"),
                "Dinner at beach restaurant", Expense.SplitType.EQUAL);
        Expense cab = new Expense(1, "9876543212", new BigDecimal("1200.00"),
                "Airport to hotel cab", Expense.SplitType.EQUAL);

        addExpense(hotel);
        addExpense(dinner);
        addExpense(cab);

        // Create expenses for Roommates
        Expense electricity = new Expense(2, "9876543210", new BigDecimal("3000.00"),
                "Electricity bill - January", Expense.SplitType.EQUAL);
        Expense groceries = new Expense(2, "9876543214", new BigDecimal("1500.00"),
                "Weekly groceries", Expense.SplitType.EQUAL);

        addExpense(electricity);
        addExpense(groceries);

        // Create expense participants (equal splits)
        // Hotel: 8000 / 4 = 2000 each
        addParticipant(new ExpenseParticipant(1, "9876543210", new BigDecimal("2000.00")));
        addParticipant(new ExpenseParticipant(1, "9876543211", new BigDecimal("2000.00")));
        addParticipant(new ExpenseParticipant(1, "9876543212", new BigDecimal("2000.00")));
        addParticipant(new ExpenseParticipant(1, "9876543213", new BigDecimal("2000.00")));

        // Dinner: 2400 / 4 = 600 each
        addParticipant(new ExpenseParticipant(2, "9876543210", new BigDecimal("600.00")));
        addParticipant(new ExpenseParticipant(2, "9876543211", new BigDecimal("600.00")));
        addParticipant(new ExpenseParticipant(2, "9876543212", new BigDecimal("600.00")));
        addParticipant(new ExpenseParticipant(2, "9876543213", new BigDecimal("600.00")));

        // Cab: 1200 / 4 = 300 each
        addParticipant(new ExpenseParticipant(3, "9876543210", new BigDecimal("300.00")));
        addParticipant(new ExpenseParticipant(3, "9876543211", new BigDecimal("300.00")));
        addParticipant(new ExpenseParticipant(3, "9876543212", new BigDecimal("300.00")));
        addParticipant(new ExpenseParticipant(3, "9876543213", new BigDecimal("300.00")));

        // Electricity: 3000 / 3 = 1000 each
        addParticipant(new ExpenseParticipant(4, "9876543210", new BigDecimal("1000.00")));
        addParticipant(new ExpenseParticipant(4, "9876543211", new BigDecimal("1000.00")));
        addParticipant(new ExpenseParticipant(4, "9876543214", new BigDecimal("1000.00")));

        // Groceries: 1500 / 3 = 500 each
        addParticipant(new ExpenseParticipant(5, "9876543210", new BigDecimal("500.00")));
        addParticipant(new ExpenseParticipant(5, "9876543211", new BigDecimal("500.00")));
        addParticipant(new ExpenseParticipant(5, "9876543214", new BigDecimal("500.00")));

        // Create some transactions (payments)
        addTransaction(
                new Transaction("9876543211", "9876543210", new BigDecimal("2000.00"), 1, "Payment for hotel share"));
        addTransaction(
                new Transaction("9876543212", "9876543210", new BigDecimal("1000.00"), 1, "Partial payment for hotel"));

        // Create settlements (Goa Trip)
        addSettlement(new Settlement(1, "9876543211", "9876543210", new BigDecimal("0.00"))); // Priya settled with
                                                                                              // Rahul
        settlements.get(1).setStatus(Settlement.SettlementStatus.SETTLED);

        addSettlement(new Settlement(1, "9876543212", "9876543210", new BigDecimal("1000.00"))); // Amit still owes 1000
        settlements.get(2).setStatus(Settlement.SettlementStatus.PARTIAL);

        addSettlement(new Settlement(1, "9876543213", "9876543210", new BigDecimal("2000.00"))); // Sneha owes 2000
        addSettlement(new Settlement(1, "9876543210", "9876543211", new BigDecimal("600.00"))); // Rahul owes Priya
        addSettlement(new Settlement(1, "9876543212", "9876543211", new BigDecimal("600.00"))); // Amit owes Priya
        addSettlement(new Settlement(1, "9876543213", "9876543211", new BigDecimal("600.00"))); // Sneha owes Priya
        addSettlement(new Settlement(1, "9876543210", "9876543212", new BigDecimal("300.00"))); // Rahul owes Amit
        addSettlement(new Settlement(1, "9876543211", "9876543212", new BigDecimal("300.00"))); // Priya owes Amit
        addSettlement(new Settlement(1, "9876543213", "9876543212", new BigDecimal("300.00"))); // Sneha owes Amit

        // Create settlements (Roommates)
        addSettlement(new Settlement(2, "9876543211", "9876543210", new BigDecimal("1000.00"))); // Priya owes Rahul
        addSettlement(new Settlement(2, "9876543214", "9876543210", new BigDecimal("1000.00"))); // Vikram owes Rahul
        addSettlement(new Settlement(2, "9876543210", "9876543214", new BigDecimal("500.00"))); // Rahul owes Vikram
        addSettlement(new Settlement(2, "9876543211", "9876543214", new BigDecimal("500.00"))); // Priya owes Vikram
    }

    // User operations
    public String addUser(User user) {
        String phone = user.getPhoneNumber();
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        users.put(phone, user);
        return phone;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    // Group operations
    public int addGroup(Group group) {
        int id = groupIdCounter.incrementAndGet();
        group.setGroupId(id);
        group.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        groups.put(id, group);
        return id;
    }

    public Map<Integer, Group> getGroups() {
        return groups;
    }

    // GroupMember operations
    public int addGroupMember(GroupMember member) {
        int id = groupMemberIdCounter.incrementAndGet();
        member.setId(id);
        member.setJoinedAt(new Timestamp(System.currentTimeMillis()));
        groupMembers.put(id, member);
        return id;
    }

    public Map<Integer, GroupMember> getGroupMembers() {
        return groupMembers;
    }

    // Expense operations
    public int addExpense(Expense expense) {
        int id = expenseIdCounter.incrementAndGet();
        expense.setExpenseId(id);
        expense.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        expenses.put(id, expense);
        return id;
    }

    public Map<Integer, Expense> getExpenses() {
        return expenses;
    }

    // ExpenseParticipant operations
    public int addParticipant(ExpenseParticipant participant) {
        int id = expenseParticipantIdCounter.incrementAndGet();
        participant.setId(id);
        expenseParticipants.put(id, participant);
        return id;
    }

    public Map<Integer, ExpenseParticipant> getExpenseParticipants() {
        return expenseParticipants;
    }

    // Transaction operations
    public int addTransaction(Transaction transaction) {
        int id = transactionIdCounter.incrementAndGet();
        transaction.setTransactionId(id);
        transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        transactions.put(id, transaction);
        return id;
    }

    public Map<Integer, Transaction> getTransactions() {
        return transactions;
    }

    // Settlement operations
    public int addSettlement(Settlement settlement) {
        int id = settlementIdCounter.incrementAndGet();
        settlement.setId(id);
        settlement.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        settlements.put(id, settlement);
        return id;
    }

    public Map<Integer, Settlement> getSettlements() {
        return settlements;
    }

    // Reset data (for testing)
    public void reset() {
        users.clear();
        groups.clear();
        groupMembers.clear();
        expenses.clear();
        expenseParticipants.clear();
        transactions.clear();
        settlements.clear();

        // userIdCounter.set(0); // Removed
        groupIdCounter.set(0);
        groupMemberIdCounter.set(0);
        expenseIdCounter.set(0);
        expenseParticipantIdCounter.set(0);
        transactionIdCounter.set(0);
        settlementIdCounter.set(0);

        initializeSampleData();
    }
}
