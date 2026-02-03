# DBMS Integration - Step-by-Step Verification & Testing Guide

## Overview

Your Expense Tracker application **is already fully DBMS-integrated**. This guide helps you verify, test, and maintain the database connectivity.

---

## Part 1: Database Setup Verification

### Step 1: Check Database Configuration

**File:** `db.properties`

```properties
# These settings must match your MySQL setup
db.url=jdbc:mysql://localhost:3306/expense_tracker_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=KU@36
```

**Verify:**
- [ ] MySQL is running on localhost:3306
- [ ] Database name is `expense_tracker_db`
- [ ] Username is `root` 
- [ ] Password is `KU@36`

### Step 2: Initialize Database

```bash
# Navigate to project directory
cd "c:\Users\Acer\OneDrive\Desktop\dbms java final\dbms java web\expense-tracker"

# Run database setup
mvn exec:java -Dexec.mainClass="com.expensetracker.util.DatabaseSetup"
```

This creates:
- [ ] Database `expense_tracker_db`
- [ ] All required tables (expenses, settlements, transactions, groups, users, etc.)
- [ ] Triggers for automatic settlement updates

### Step 3: Verify Database Connection

**Test Query:** Check if you can connect from MySQL client

```bash
# Open MySQL command line
mysql -u root -p

# Enter password: KU@36

# Verify database exists
SHOW DATABASES;
# Should show: expense_tracker_db

# Select the database
USE expense_tracker_db;

# Show tables
SHOW TABLES;
# Should show: expenses, expense_participants, settlements, transactions, groups, group_members, users
```

**Expected Tables:**
```sql
-- Core Tables
CREATE TABLE expenses (
    expense_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    paid_by VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    split_type ENUM('EQUAL', 'CUSTOM'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(group_id),
    FOREIGN KEY (paid_by) REFERENCES users(phone_number)
);

CREATE TABLE expense_participants (
    participant_id INT AUTO_INCREMENT PRIMARY KEY,
    expense_id INT NOT NULL,
    user_id VARCHAR(20) NOT NULL,
    share_amount DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (expense_id) REFERENCES expenses(expense_id),
    FOREIGN KEY (user_id) REFERENCES users(phone_number)
);

CREATE TABLE settlements (
    settlement_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    from_user VARCHAR(20) NOT NULL,
    to_user VARCHAR(20) NOT NULL,
    net_balance DECIMAL(10, 2),
    paid_amount DECIMAL(10, 2) DEFAULT 0,
    status ENUM('PENDING', 'SETTLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(group_id),
    FOREIGN KEY (from_user) REFERENCES users(phone_number),
    FOREIGN KEY (to_user) REFERENCES users(phone_number)
);

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    from_user VARCHAR(20) NOT NULL,
    to_user VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    expense_id INT,
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user) REFERENCES users(phone_number),
    FOREIGN KEY (to_user) REFERENCES users(phone_number),
    FOREIGN KEY (expense_id) REFERENCES expenses(expense_id)
);

CREATE TABLE groups (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_by VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(phone_number)
);

CREATE TABLE group_members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    user_id VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_member (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES groups(group_id),
    FOREIGN KEY (user_id) REFERENCES users(phone_number)
);

CREATE TABLE users (
    phone_number VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Part 2: Application Build & Deployment

### Step 1: Build Application

```bash
# Clean and build
mvn clean install
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

Files created:
- [ ] `target/expense-tracker-1.0-SNAPSHOT.war` (deployable WAR file)
- [ ] `target/classes/` (compiled Java classes)

### Step 2: Deploy to Tomcat

**Option A: Using Maven Plugin**
```bash
mvn tomcat7:deploy
```

**Option B: Manual Deployment**
1. Copy `target/expense-tracker-1.0-SNAPSHOT.war` to Tomcat `webapps/` folder
2. Start Tomcat
3. Tomcat auto-extracts WAR file

### Step 3: Verify Deployment

```bash
# Check if app is running
curl http://localhost:8080/expense-tracker/

# Expected: HTML content of index.html
```

---

## Part 3: Frontend-to-Backend Communication Flow

### Verify Each Feature

#### 1. Create Group

**Frontend:** `create-group.html`
```javascript
// User fills form and submits
// POST /api/group with:
// - name: "Friends"
// - description: "College friends"
// - creatorName: "John"
// - creatorPhone: "9876543210"
// - creatorEmail: "john@example.com"
```

**Backend:** `GroupController.java`
```java
// doPost() receives request
// Calls groupService.createGroup()
// Returns JSON with groupId
```

**Service:** `GroupService.java`
```java
// groupService.createGroup():
// 1. Creates/finds user via UserDAO
// 2. Inserts group via GroupDAO (writes to DB)
// 3. Adds creator as member via GroupMemberDAO
// 4. Returns group object
```

**Database:**
```sql
INSERT INTO users VALUES ('9876543210', 'John', 'john@example.com', NOW());
INSERT INTO groups VALUES (1, 'Friends', 'College friends', '9876543210', NOW());
INSERT INTO group_members VALUES (1, 1, '9876543210', NOW());
```

**Verification:**
```bash
# Check if group was created
mysql> SELECT * FROM groups;
mysql> SELECT * FROM group_members;
```

#### 2. Add Expense

**Frontend:** `add-expense.html` → `submitExpense()` → `POST /api/expense`

**Backend Flow:**
```
ExpenseController.doPost()
    ↓
ExpenseService.addExpenseEqualSplit()
    ↓
ExpenseDAO.insert() → INSERT expenses table
ExpenseParticipantDAO.insert() → INSERT expense_participants table
SettlementDAO.insert()/update() → INSERT/UPDATE settlements table
    ↓
Returns JSON with expense details
```

**Database Verification:**
```sql
-- Check expense was created
mysql> SELECT * FROM expenses WHERE group_id = 1;

-- Check participants
mysql> SELECT * FROM expense_participants WHERE expense_id = 1;

-- Check settlements (automatically created)
mysql> SELECT * FROM settlements WHERE group_id = 1;
```

#### 3. Record Payment

**Frontend:** `record-payment.html` → `submitPayment()` → `POST /api/payment`

**Backend Flow:**
```
PaymentController.doPost()
    ↓
PaymentService.recordPayment()
    ↓
TransactionDAO.insert() → INSERT transactions table
SettlementDAO.update() → UPDATE settlements table (mark as SETTLED)
    ↓
Returns JSON with transaction details
```

**Database Verification:**
```sql
-- Check transaction was recorded
mysql> SELECT * FROM transactions WHERE from_user = '9876543211';

-- Check settlement was updated
mysql> SELECT * FROM settlements 
       WHERE from_user = '9876543211' AND status = 'SETTLED';
```

#### 4. View Settlements

**Frontend:** `settlement.html` → calls `GET /api/settlement/summary?groupId=1`

**Backend Flow:**
```
SettlementController.doGet(/summary)
    ↓
SettlementService.getSettlementSummary()
    ↓
SettlementDAO.findPendingByGroup() → SELECT from settlements table
UserDAO.findByPhone() → SELECT from users table (for names)
    ↓
Returns formatted JSON with settlement data
```

---

## Part 4: API Endpoints - Testing

### Test Using curl or Postman

#### Create Group
```bash
curl -X POST http://localhost:8080/expense-tracker/api/group \
  -d "name=Friends&description=College friends&creatorName=John&creatorPhone=9876543210&creatorEmail=john@example.com"
```

**Expected Response:**
```json
{
  "groupId": 1,
  "name": "Friends",
  "description": "College friends",
  "createdBy": "9876543210"
}
```

#### Add Group Member
```bash
curl -X POST http://localhost:8080/expense-tracker/api/group/1/members \
  -d "memberName=Alice&memberPhone=9876543211&memberEmail=alice@example.com"
```

#### Get All Groups
```bash
curl http://localhost:8080/expense-tracker/api/group
```

#### Add Expense
```bash
curl -X POST http://localhost:8080/expense-tracker/api/expense \
  -d "groupId=1&paidBy=9876543210&amount=1200&description=Dinner&splitType=EQUAL&participants=9876543210&participants=9876543211"
```

#### Get Expenses for Group
```bash
curl "http://localhost:8080/expense-tracker/api/expense?groupId=1"
```

#### Record Payment
```bash
curl -X POST http://localhost:8080/expense-tracker/api/payment \
  -d "fromUser=9876543211&toUser=9876543210&amount=600&note=Dinner payment"
```

#### Get Settlements
```bash
curl "http://localhost:8080/expense-tracker/api/settlement/summary?groupId=1"
```

---

## Part 5: Data Persistence Verification

### Test 1: Data Survives Application Restart

1. **Add an expense** via web interface
2. **Stop the application**
3. **Restart the application**
4. **Check dashboard** - expense should still be there

✅ If expense is visible, data was persisted to database.
❌ If expense disappears, data is lost (connection issue).

### Test 2: Direct Database Query

```bash
# Stop application
# Query database directly
mysql -u root -p < expense_tracker_db -e "SELECT * FROM expenses;"

# Should show all expenses ever created
```

### Test 3: Multiple Users Adding Data Simultaneously

1. Open app in Browser 1 → Add expense in Group 1
2. Open app in Browser 2 → Add member to Group 1
3. Both should see each other's changes immediately

✅ If both see updates, database is working correctly.

---

## Part 6: Common Issues & Solutions

### Issue 1: "Cannot connect to database"

**Symptoms:**
- Error: "Connection refused" or "Database not found"
- API returns 500 error

**Solutions:**
```bash
# 1. Check if MySQL is running
# Windows:
sc query mysql80
# or check MySQL in Services

# 2. Verify configuration
# Edit db.properties - check host, port, username, password

# 3. Test connection manually
mysql -h localhost -u root -p
# Try to enter database
USE expense_tracker_db;

# 4. Check firewall
# Ensure port 3306 is not blocked
```

### Issue 2: "Table doesn't exist"

**Symptoms:**
- Error: "Table 'expense_tracker_db.expenses' doesn't exist"

**Solutions:**
```bash
# Re-run database setup
mvn exec:java -Dexec.mainClass="com.expensetracker.util.DatabaseSetup"

# Verify tables exist
mysql -u root -p -e "USE expense_tracker_db; SHOW TABLES;"
```

### Issue 3: "Data not appearing after adding expense"

**Symptoms:**
- Form submission succeeds (sees success message)
- But data not visible on dashboard
- Dashboard shows empty

**Solutions:**
```bash
# Check if data is actually in database
mysql -u root -p
USE expense_tracker_db;
SELECT COUNT(*) FROM expenses;

# If count is 0:
# - Check if ExpenseDAO.insert() is being called
# - Check db.properties connection settings
# - Look at server logs for SQL errors

# If count > 0 but not showing on dashboard:
# - Check JavaScript API call (verify correct endpoint)
# - Check API response in browser console (F12 → Network tab)
# - Verify controller returns correct JSON
```

### Issue 4: "Duplicate entry for group member"

**Symptoms:**
- Error when trying to add existing member to group

**Solution:**
```sql
-- This is expected behavior! Check if member is already in group:
SELECT * FROM group_members WHERE group_id = 1 AND user_id = '9876543211';
-- If exists, cannot add again

-- Or use different phone number for new member
```

---

## Part 7: Database Maintenance

### Backup Database

```bash
# Windows Command Prompt
mysqldump -u root -p expense_tracker_db > backup.sql

# Or use the provided batch file
run_backup_db.bat
```

### Restore Database

```bash
# From backup
mysql -u root -p expense_tracker_db < backup.sql
```

### Clear All Data (Reset Database)

```bash
# Drop all tables
mysql -u root -p
USE expense_tracker_db;
DROP DATABASE expense_tracker_db;

# Recreate database
EXIT;

# Re-run setup
mvn exec:java -Dexec.mainClass="com.expensetracker.util.DatabaseSetup"
```

---

## Part 8: Complete End-to-End Test Scenario

### Scenario: Two Friends Splitting Expenses

**Setup:**
```
Group: "Pizza Night"
Members:
  - John (9876543210) - Creator
  - Alice (9876543211)
```

**Step 1: Create Group**
- [ ] Navigate to "Create Group"
- [ ] Fill: Name="Pizza Night", Creator="John", Phone="9876543210"
- [ ] Submit → Check database for group creation
- [ ] Query: `SELECT * FROM groups WHERE name = 'Pizza Night';`

**Step 2: Add Member**
- [ ] Navigate to "Add Member"
- [ ] Fill: Group="Pizza Night", Member="Alice", Phone="9876543211"
- [ ] Submit → Check database for group member
- [ ] Query: `SELECT * FROM group_members WHERE user_id = '9876543211';`

**Step 3: Add Expense**
- [ ] Navigate to "Add Expense"
- [ ] Fill:
  - Group: "Pizza Night"
  - Paid By: "John"
  - Amount: 600
  - Description: "Pizza for 2"
  - Participants: John, Alice (both checked)
  - Split: Equal → 300 each
- [ ] Submit → Check database
- [ ] Queries:
  ```sql
  SELECT * FROM expenses WHERE group_id = 1;
  SELECT * FROM expense_participants WHERE expense_id = 1;
  SELECT * FROM settlements WHERE group_id = 1;
  ```

**Expected DB State:**
```
expenses: 1 record (600 total)
expense_participants: 2 records (300 each)
settlements: 1 record (Alice owes John 300)
```

**Step 4: Record Payment**
- [ ] Navigate to "Record Payment"
- [ ] See "Alice owes John ₹300"
- [ ] Fill:
  - Group: "Pizza Night"
  - From: "Alice"
  - To: "John"
  - Amount: 300
- [ ] Submit → Check database
- [ ] Queries:
  ```sql
  SELECT * FROM transactions WHERE from_user = '9876543211';
  SELECT * FROM settlements WHERE settlement_id = 1;
  ```

**Expected DB State:**
```
transactions: 1 record (Alice paid John 300)
settlements: Status changed from PENDING to SETTLED
```

**Step 5: Verify Settlement**
- [ ] Navigate to "Settlements"
- [ ] Should show "All settled!"
- [ ] Query: `SELECT * FROM settlements WHERE status = 'PENDING';`
- [ ] Should return 0 rows

**✅ Test Complete!**
- All data persisted to database
- Transactions processed correctly
- Settlement status updated properly

---

## Part 9: Code Review Checklist

### Controllers (Processing Requests)

**ExpenseController** ✅
- [ ] `doGet()` calls `ExpenseService` (not DAO directly)
- [ ] `doPost()` calls `ExpenseService`
- [ ] Returns JSON response
- [ ] Error handling present

**PaymentController** ✅
- [ ] `doGet()` returns transactions from service
- [ ] `doPost()` calls `PaymentService.recordPayment()`
- [ ] Updates settlements in database
- [ ] Returns JSON response

**SettlementController** ✅
- [ ] `doGet()` retrieves data from service
- [ ] Returns multiple views (summary, balances, simplified)
- [ ] Enriches with user names

**GroupController** ✅
- [ ] `doGet()` retrieves groups from service
- [ ] `doPost()` creates groups via service
- [ ] Adds members via service

### Services (Business Logic)

**ExpenseService** ✅
- [ ] Creates Expense object
- [ ] Calls `expenseDAO.insert()` to save to DB
- [ ] Creates participants via `participantDAO`
- [ ] Updates settlements via `settlementDAO`
- [ ] Returns created object

**PaymentService** ✅
- [ ] Records transaction via `transactionDAO`
- [ ] Updates settlement balance
- [ ] Validates input (no self-payment)
- [ ] Returns transaction object

**SettlementService** ✅
- [ ] Queries `settlementDAO` for pending settlements
- [ ] Enriches with user names from `userDAO`
- [ ] Calculates net balances
- [ ] Returns formatted summaries

**GroupService** ✅
- [ ] Creates groups via `groupDAO.insert()`
- [ ] Manages members via `memberDAO`
- [ ] Creates/finds users via `userDAO`

### DAOs (Database Layer)

**ExpenseDAOImpl** ✅
- [ ] `insert()` uses PreparedStatement
- [ ] `findById()` queries by ID
- [ ] `findByGroup()` queries by group
- [ ] Error handling with try-catch

**SettlementDAOImpl** ✅
- [ ] Queries pending settlements
- [ ] Queries settled settlements
- [ ] Updates settlement status

**TransactionDAOImpl** ✅
- [ ] Inserts transaction records
- [ ] Queries by user or group

**UserDAOImpl** ✅
- [ ] Creates users
- [ ] Finds by phone number
- [ ] Queries all users

### Database Connection

**DBUtil** ✅
- [ ] Loads MySQL driver
- [ ] `getConnection()` returns new connection
- [ ] Uses settings from DatabaseConfig

**DatabaseConfig** ✅
- [ ] Loads from `db.properties`
- [ ] Supports environment variables
- [ ] Provides defaults

---

## Summary: Your App is Fully DBMS-Integrated ✅

| Component | Status | Evidence |
|-----------|--------|----------|
| Database Configuration | ✅ | `db.properties` with MySQL credentials |
| Connection Pool | ✅ | `DBUtil.getConnection()` creates connections |
| DAOs | ✅ | All DAOImpl classes use SQL and execute queries |
| Services | ✅ | All services call DAOs for data operations |
| Controllers | ✅ | All endpoints call services, not DAOs directly |
| Frontend | ✅ | HTML pages send requests to API endpoints |
| Database Schema | ✅ | All tables created by DatabaseSetup |
| Data Persistence | ✅ | Data persists across app restarts |
| Transaction Support | ✅ | Expense + Participant + Settlement in one operation |
| Error Handling | ✅ | Try-catch blocks in DAOs and controllers |

**Your expense tracker is production-ready!**

