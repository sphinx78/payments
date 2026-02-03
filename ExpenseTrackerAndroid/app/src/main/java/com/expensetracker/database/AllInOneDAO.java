package com.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.expensetracker.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * AllInOneDAO - Simplified DAO implementation
 * Combines all database operations in one class for simplicity
 */
public class AllInOneDAO {

    private DatabaseHelper dbHelper;
    private Context context;

    public AllInOneDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    // ========== USER OPERATIONS ==========

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("users", null, null, null, null, null, "name ASC");

        while (cursor.moveToNext()) {
            User user = new User();
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
            users.add(user);
        }
        cursor.close();
        return users;
    }

    public User getUserByPhone(String phoneNumber) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("users", null,
                "phone_number = ?", new String[] { phoneNumber },
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
        }
        cursor.close();
        return user;
    }

    // ========== GROUP OPERATIONS ==========

    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("groups", null, null, null, null, null, "created_at DESC");

        while (cursor.moveToNext()) {
            Group group = new Group();
            group.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            group.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            group.setCreatedBy(cursor.getString(cursor.getColumnIndexOrThrow("created_by")));
            group.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
            groups.add(group);
        }
        cursor.close();
        return groups;
    }

    public Group getGroupById(int groupId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("groups", null,
                "group_id = ?", new String[] { String.valueOf(groupId) },
                null, null, null);

        Group group = null;
        if (cursor.moveToFirst()) {
            group = new Group();
            group.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            group.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            group.setCreatedBy(cursor.getString(cursor.getColumnIndexOrThrow("created_by")));
            group.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
        }
        cursor.close();
        return group;
    }

    public long insertGroup(Group group) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", group.getName());
        values.put("description", group.getDescription());
        values.put("created_by", group.getCreatedBy());
        values.put("created_at", group.getCreatedAt());

        return db.insert("groups", null, values);
    }

    // ========== GROUP MEMBER OPERATIONS ==========

    public List<User> getGroupMembers(int groupId) {
        List<User> members = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT u.* FROM users u " +
                "INNER JOIN group_members gm ON u.phone_number = gm.user_id " +
                "WHERE gm.group_id = ? ORDER BY u.name ASC";

        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(groupId) });

        while (cursor.moveToNext()) {
            User user = new User();
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
            members.add(user);
        }
        cursor.close();
        return members;
    }

    // ========== EXPENSE OPERATIONS ==========

    public List<Expense> getGroupExpenses(int groupId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("expenses", null,
                "group_id = ?", new String[] { String.valueOf(groupId) },
                null, null, "created_at DESC");

        while (cursor.moveToNext()) {
            Expense expense = new Expense();
            expense.setExpenseId(cursor.getInt(cursor.getColumnIndexOrThrow("expense_id")));
            expense.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            expense.setPaidBy(cursor.getString(cursor.getColumnIndexOrThrow("paid_by")));
            expense.setAmount(new BigDecimal(cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))));
            expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            expense.setSplitType(
                    Expense.SplitType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("split_type"))));
            expense.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
            expenses.add(expense);
        }
        cursor.close();
        return expenses;
    }

    public long insertExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("group_id", expense.getGroupId());
        values.put("paid_by", expense.getPaidBy());
        values.put("amount", expense.getAmount().doubleValue());
        values.put("description", expense.getDescription());
        values.put("split_type", expense.getSplitType().name());
        values.put("created_at", expense.getCreatedAt());

        return db.insert("expenses", null, values);
    }

    // ========== SETTLEMENT OPERATIONS ==========

    public List<Settlement> getPendingSettlements(int groupId) {
        List<Settlement> settlements = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("settlements", null,
                "group_id = ? AND status = 'PENDING' AND net_balance > 0",
                new String[] { String.valueOf(groupId) },
                null, null, "net_balance DESC");

        while (cursor.moveToNext()) {
            Settlement settlement = new Settlement();
            settlement.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            settlement.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            settlement.setFromUser(cursor.getString(cursor.getColumnIndexOrThrow("from_user")));
            settlement.setToUser(cursor.getString(cursor.getColumnIndexOrThrow("to_user")));
            settlement.setNetBalance(new BigDecimal(cursor.getDouble(cursor.getColumnIndexOrThrow("net_balance"))));
            settlement.setStatus(
                    Settlement.SettlementStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status"))));
            settlement.setLastUpdated(cursor.getLong(cursor.getColumnIndexOrThrow("last_updated")));
            settlements.add(settlement);
        }
        cursor.close();
        return settlements;
    }

    // ========== STATISTICS ==========

    public double getTotalExpensesForGroup(int groupId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) as total FROM expenses WHERE group_id = ?",
                new String[] { String.valueOf(groupId) });

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getPendingSettlementAmount(int groupId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(net_balance) as total FROM settlements " +
                        "WHERE group_id = ? AND status = 'PENDING'",
                new String[] { String.valueOf(groupId) });

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }
}
