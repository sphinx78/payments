package com.expensetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper - SQLite Database Manager
 * 
 * Migrated from MySQL schema.sql
 * Creates and manages all database tables
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    // Singleton pattern
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        db.execSQL("CREATE TABLE users (" +
                "phone_number TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "created_at INTEGER DEFAULT 0)");

        // Create Groups table
        db.execSQL("CREATE TABLE groups (" +
                "group_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "created_by TEXT NOT NULL, " +
                "created_at INTEGER DEFAULT 0, " +
                "FOREIGN KEY(created_by) REFERENCES users(phone_number))");

        // Create GroupMembers table (many-to-many junction)
        db.execSQL("CREATE TABLE group_members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "group_id INTEGER NOT NULL, " +
                "user_id TEXT NOT NULL, " +
                "joined_at INTEGER DEFAULT 0, " +
                "UNIQUE(group_id, user_id), " +
                "FOREIGN KEY(group_id) REFERENCES groups(group_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(user_id) REFERENCES users(phone_number) ON DELETE CASCADE)");

        // Create Expenses table
        db.execSQL("CREATE TABLE expenses (" +
                "expense_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "group_id INTEGER NOT NULL, " +
                "paid_by TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "description TEXT NOT NULL, " +
                "split_type TEXT NOT NULL DEFAULT 'EQUAL', " +
                "created_at INTEGER DEFAULT 0, " +
                "FOREIGN KEY(group_id) REFERENCES groups(group_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(paid_by) REFERENCES users(phone_number))");

        // Create ExpenseParticipants table
        db.execSQL("CREATE TABLE expense_participants (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "expense_id INTEGER NOT NULL, " +
                "user_id TEXT NOT NULL, " +
                "share_amount REAL NOT NULL, " +
                "is_settled INTEGER DEFAULT 0, " +
                "UNIQUE(expense_id, user_id), " +
                "FOREIGN KEY(expense_id) REFERENCES expenses(expense_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(user_id) REFERENCES users(phone_number) ON DELETE CASCADE)");

        // Create Transactions table
        db.execSQL("CREATE TABLE transactions (" +
                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "from_user TEXT NOT NULL, " +
                "to_user TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "expense_id INTEGER, " +
                "note TEXT, " +
                "created_at INTEGER DEFAULT 0, " +
                "FOREIGN KEY(from_user) REFERENCES users(phone_number), " +
                "FOREIGN KEY(to_user) REFERENCES users(phone_number), " +
                "FOREIGN KEY(expense_id) REFERENCES expenses(expense_id))");

        // Create Settlements table
        db.execSQL("CREATE TABLE settlements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "group_id INTEGER NOT NULL, " +
                "from_user TEXT NOT NULL, " +
                "to_user TEXT NOT NULL, " +
                "net_balance REAL DEFAULT 0.00, " +
                "status TEXT DEFAULT 'PENDING', " +
                "last_updated INTEGER DEFAULT 0, " +
                "UNIQUE(group_id, from_user, to_user), " +
                "FOREIGN KEY(group_id) REFERENCES groups(group_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(from_user) REFERENCES users(phone_number) ON DELETE CASCADE, " +
                "FOREIGN KEY(to_user) REFERENCES users(phone_number) ON DELETE CASCADE)");

        // Create indexes for performance
        db.execSQL("CREATE INDEX idx_expenses_group ON expenses(group_id)");
        db.execSQL("CREATE INDEX idx_expenses_paid_by ON expenses(paid_by)");
        db.execSQL("CREATE INDEX idx_transactions_from ON transactions(from_user)");
        db.execSQL("CREATE INDEX idx_transactions_to ON transactions(to_user)");

        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate
        db.execSQL("DROP TABLE IF EXISTS settlements");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS expense_participants");
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS group_members");
        db.execSQL("DROP TABLE IF EXISTS groups");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    /**
     * Insert sample data for demo purposes
     * Matches the sample data from the web app
     */
    private void insertSampleData(SQLiteDatabase db) {
        long timestamp = System.currentTimeMillis();

        // Insert 5 sample users
        db.execSQL("INSERT INTO users (phone_number, name, email, created_at) VALUES " +
                "('9841234567', 'Alice', 'alice@example.com', " + timestamp + ")");
        db.execSQL("INSERT INTO users (phone_number, name, email, created_at) VALUES " +
                "('9841234568', 'Bob', 'bob@example.com', " + timestamp + ")");
        db.execSQL("INSERT INTO users (phone_number, name, email, created_at) VALUES " +
                "('9841234569', 'Charlie', 'charlie@example.com', " + timestamp + ")");
        db.execSQL("INSERT INTO users (phone_number, name, email, created_at) VALUES " +
                "('9841234570', 'Diana', 'diana@example.com', " + timestamp + ")");
        db.execSQL("INSERT INTO users (phone_number, name, email, created_at) VALUES " +
                "('9841234571', 'Eve', 'eve@example.com', " + timestamp + ")");

        // Insert 2 sample groups
        db.execSQL("INSERT INTO groups (name, description, created_by, created_at) VALUES " +
                "('Roommates', 'Shared apartment expenses', '9841234567', " + timestamp + ")");
        db.execSQL("INSERT INTO groups (name, description, created_by, created_at) VALUES " +
                "('Trip to Goa', 'Beach vacation 2026', '9841234568', " + timestamp + ")");

        // Add members to groups
        // Roommates: Alice, Bob, Charlie
        db.execSQL(
                "INSERT INTO group_members (group_id, user_id, joined_at) VALUES (1, '9841234567', " + timestamp + ")");
        db.execSQL(
                "INSERT INTO group_members (group_id, user_id, joined_at) VALUES (1, '9841234568', " + timestamp + ")");
        db.execSQL(
                "INSERT INTO group_members (group_id, user_id, joined_at) VALUES (1, '9841234569', " + timestamp + ")");

        // Trip to Goa: Bob, Diana, Eve
        db.execSQL(
                "INSERT INTO group_members (group_id, user_id, joined_at) VALUES (2, '9841234568', " + timestamp + ")");
        db.execSQL(
                "INSERT INTO group_members (group_id, user_id, joined_at) VALUES (2, '9841234570', " + timestamp + ")");
        db.execSQL(
                "INSERT INTO group_members (group_id, user_id, joined_at) VALUES (2, '9841234571', " + timestamp + ")");

        // Sample expenses for Roommates group
        db.execSQL("INSERT INTO expenses (group_id, paid_by, amount, description, split_type, created_at) VALUES " +
                "(1, '9841234567', 600.00, 'Groceries', 'EQUAL', " + timestamp + ")");
        db.execSQL("INSERT INTO expenses (group_id, paid_by, amount, description, split_type, created_at) VALUES " +
                "(1, '9841234568', 1500.00, 'Electricity Bill', 'EQUAL', " + timestamp + ")");

        // Sample expenses for Trip to Goa
        db.execSQL("INSERT INTO expenses (group_id, paid_by, amount, description, split_type, created_at) VALUES " +
                "(2, '9841234570', 9000.00, 'Hotel Booking', 'EQUAL', " + timestamp + ")");

        // Expense participants (equal split)
        // Expense 1: ₹600 / 3 = ₹200 each
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (1, '9841234567', 200.00)");
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (1, '9841234568', 200.00)");
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (1, '9841234569', 200.00)");

        // Expense 2: ₹1500 / 3 = ₹500 each
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (2, '9841234567', 500.00)");
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (2, '9841234568', 500.00)");
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (2, '9841234569', 500.00)");

        // Expense 3: ₹9000 / 3 = ₹3000 each
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (3, '9841234568', 3000.00)");
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (3, '9841234570', 3000.00)");
        db.execSQL(
                "INSERT INTO expense_participants (expense_id, user_id, share_amount) VALUES (3, '9841234571', 3000.00)");

        // Settlements for Roommates
        db.execSQL("INSERT INTO settlements (group_id, from_user, to_user, net_balance, status, last_updated) VALUES " +
                "(1, '9841234568', '9841234567', 200.00, 'PENDING', " + timestamp + ")");
        db.execSQL("INSERT INTO settlements (group_id, from_user, to_user, net_balance, status, last_updated) VALUES " +
                "(1, '9841234569', '9841234567', 200.00, 'PENDING', " + timestamp + ")");
        db.execSQL("INSERT INTO settlements (group_id, from_user, to_user, net_balance, status, last_updated) VALUES " +
                "(1, '9841234567', '9841234568', 500.00, 'PENDING', " + timestamp + ")");
        db.execSQL("INSERT INTO settlements (group_id, from_user, to_user, net_balance, status, last_updated) VALUES " +
                "(1, '9841234569', '9841234568', 500.00, 'PENDING', " + timestamp + ")");

        // Settlements for Trip to Goa
        db.execSQL("INSERT INTO settlements (group_id, from_user, to_user, net_balance, status, last_updated) VALUES " +
                "(2, '9841234568', '9841234570', 3000.00, 'PENDING', " + timestamp + ")");
        db.execSQL("INSERT INTO settlements (group_id, from_user, to_user, net_balance, status, last_updated) VALUES " +
                "(2, '9841234571', '9841234570', 3000.00, 'PENDING', " + timestamp + ")");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.setForeignKeyConstraintsEnabled(true);
    }
}
