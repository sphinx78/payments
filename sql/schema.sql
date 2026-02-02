-- ============================================================
-- FRIEND GROUP EXPENSE MANAGEMENT SYSTEM - DATABASE SCHEMA
-- ============================================================
-- Academic Project: DBMS Subject
-- 
-- NORMALIZATION: All tables are in Third Normal Form (3NF)
-- - 1NF: All columns are atomic, no repeating groups
-- - 2NF: All non-key attributes fully depend on primary key
-- - 3NF: No transitive dependencies between non-key attributes
-- ============================================================

-- Drop tables if exist (for fresh setup)
DROP TABLE IF EXISTS settlements;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS expense_participants;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS group_members;
DROP TABLE IF EXISTS `groups`;
DROP TABLE IF EXISTS users;

-- ============================================================
-- TABLE: users
-- ============================================================
-- Stores user account information
-- PRIMARY KEY: user_id (auto-increment)
-- INDEXES: email (unique, for login lookups)
-- ============================================================
-- ============================================================
-- TABLE: users
-- ============================================================
-- Stores user account information
-- PRIMARY KEY: phone_number (String)
-- INDEXES: email (unique, for login lookups)
-- ============================================================
CREATE TABLE users (
    phone_number VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- INDEX for faster email lookups during authentication
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: groups
-- ============================================================
-- Stores friend group information
-- PRIMARY KEY: group_id
-- FOREIGN KEY: created_by -> users(phone_number)
-- ============================================================
CREATE TABLE `groups` (
    group_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_by VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- FK: Group creator must be a valid user
    CONSTRAINT fk_groups_created_by 
        FOREIGN KEY (created_by) REFERENCES users(phone_number)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: group_members
-- ============================================================
-- Junction table for many-to-many relationship: users <-> groups
-- This table exists to maintain 3NF (no multi-valued dependencies)
-- PRIMARY KEY: composite or surrogate
-- FOREIGN KEYS: group_id -> groups, user_id -> users
-- ============================================================
CREATE TABLE group_members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    user_id VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Prevent duplicate memberships
    UNIQUE KEY uk_group_member (group_id, user_id),
    
    -- FK: Must reference valid group
    CONSTRAINT fk_gm_group 
        FOREIGN KEY (group_id) REFERENCES `groups`(group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- FK: Must reference valid user
    CONSTRAINT fk_gm_user 
        FOREIGN KEY (user_id) REFERENCES users(phone_number)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: expenses
-- ============================================================
-- Stores expense records for a group
-- PRIMARY KEY: expense_id
-- FOREIGN KEYS: group_id -> groups, paid_by -> users
-- CHECK: split_type must be 'EQUAL' or 'CUSTOM'
-- ============================================================
CREATE TABLE expenses (
    expense_id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    paid_by VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    split_type ENUM('EQUAL', 'CUSTOM') DEFAULT 'EQUAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- CHECK: Amount must be positive
    CONSTRAINT chk_expense_amount CHECK (amount > 0),
    
    -- FK: Expense belongs to a group
    CONSTRAINT fk_expense_group 
        FOREIGN KEY (group_id) REFERENCES `groups`(group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- FK: Payer must be a valid user
    CONSTRAINT fk_expense_paid_by 
        FOREIGN KEY (paid_by) REFERENCES users(phone_number)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- INDEX for group-wise expense lookups
    INDEX idx_expenses_group (group_id),
    INDEX idx_expenses_paid_by (paid_by)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: expense_participants
-- ============================================================
-- Stores individual share for each participant in an expense
-- This table maintains 3NF by separating share data from expense
-- PRIMARY KEY: id
-- FOREIGN KEYS: expense_id -> expenses, user_id -> users
-- ============================================================
CREATE TABLE expense_participants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    expense_id INT NOT NULL,
    user_id VARCHAR(20) NOT NULL,
    share_amount DECIMAL(10, 2) NOT NULL,
    is_settled BOOLEAN DEFAULT FALSE,
    
    -- Prevent duplicate entries
    UNIQUE KEY uk_expense_participant (expense_id, user_id),
    
    -- CHECK: Share must be non-negative
    CONSTRAINT chk_share_amount CHECK (share_amount >= 0),
    
    -- FK: Must reference valid expense
    CONSTRAINT fk_ep_expense 
        FOREIGN KEY (expense_id) REFERENCES expenses(expense_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- FK: Must reference valid user
    CONSTRAINT fk_ep_user 
        FOREIGN KEY (user_id) REFERENCES users(phone_number)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: transactions
-- ============================================================
-- Records payment transactions between users
-- Supports partial payments with remaining_amount tracking
-- PRIMARY KEY: transaction_id
-- FOREIGN KEYS: from_user, to_user -> users; expense_id -> expenses
-- ============================================================
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    from_user VARCHAR(20) NOT NULL,
    to_user VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    expense_id INT,
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- CHECK: Cannot pay yourself (Removed due to MySQL FK constraint limitation)
    -- CONSTRAINT chk_different_users CHECK (from_user != to_user),
    
    -- CHECK: Amount must be positive
    CONSTRAINT chk_transaction_amount CHECK (amount > 0),
    
    -- FK: Payer must be valid user
    CONSTRAINT fk_trans_from 
        FOREIGN KEY (from_user) REFERENCES users(phone_number)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- FK: Receiver must be valid user
    CONSTRAINT fk_trans_to 
        FOREIGN KEY (to_user) REFERENCES users(phone_number)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- FK: Optional link to expense
    CONSTRAINT fk_trans_expense 
        FOREIGN KEY (expense_id) REFERENCES expenses(expense_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    
    -- INDEXES for transaction lookups
    INDEX idx_trans_from (from_user),
    INDEX idx_trans_to (to_user)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: settlements
-- ============================================================
-- Tracks net balance between users within a group
-- Used for "who owes whom" summary
-- Updated automatically via triggers when transactions occur
-- ============================================================
CREATE TABLE settlements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    from_user VARCHAR(20) NOT NULL,
    to_user VARCHAR(20) NOT NULL,
    net_balance DECIMAL(10, 2) DEFAULT 0.00,
    status ENUM('PENDING', 'PARTIAL', 'SETTLED') DEFAULT 'PENDING',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Unique constraint for each user pair per group
    UNIQUE KEY uk_settlement (group_id, from_user, to_user),
    
    -- FK: Must reference valid group
    CONSTRAINT fk_settle_group 
        FOREIGN KEY (group_id) REFERENCES `groups`(group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- FK: Debtor must be valid user
    CONSTRAINT fk_settle_from 
        FOREIGN KEY (from_user) REFERENCES users(phone_number)
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- FK: Creditor must be valid user
    CONSTRAINT fk_settle_to 
        FOREIGN KEY (to_user) REFERENCES users(phone_number)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- END OF SCHEMA
-- ============================================================
-- SUMMARY:
-- 7 Tables: users, groups, group_members, expenses, 
--           expense_participants, transactions, settlements
-- 
-- All tables in 3NF with proper constraints for data integrity
-- ============================================================
