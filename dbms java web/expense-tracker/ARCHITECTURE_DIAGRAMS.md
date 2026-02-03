# DBMS Integration - Visual Architecture Diagrams

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          CLIENT LAYER                                   │
│                                                                          │
│  Browser User Interface                                                 │
│  ├─ index.html (Dashboard)                                              │
│  ├─ add-expense.html (Add Expenses)                                     │
│  ├─ record-payment.html (Record Payments)                               │
│  ├─ settlement.html (View Settlements)                                  │
│  ├─ create-group.html (Create Groups)                                   │
│  └─ add-member.html (Add Members)                                       │
│                                                                          │
│  JavaScript (Fetch API)                                                 │
│  └─ Sends HTTP requests to API endpoints                                │
└────────────────┬─────────────────────────────────────────────────────────┘
                 │ HTTP Requests/Responses
                 │ JSON data
                 │
┌────────────────▼─────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                                  │
│                      (Tomcat Web Server)                                │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │ CONTROLLER LAYER (Servlet Controllers)                           │   │
│  │                                                                  │   │
│  │ ├─ @WebServlet("/api/expense")                                  │   │
│  │ │  └─ ExpenseController                                         │   │
│  │ │     ├─ doGet() → getExpensesByGroup()                         │   │
│  │ │     └─ doPost() → addExpense()                                │   │
│  │ │                                                               │   │
│  │ ├─ @WebServlet("/api/payment")                                  │   │
│  │ │  └─ PaymentController                                         │   │
│  │ │     ├─ doGet() → getTransactions()                            │   │
│  │ │     └─ doPost() → recordPayment()                             │   │
│  │ │                                                               │   │
│  │ ├─ @WebServlet("/api/settlement")                               │   │
│  │ │  └─ SettlementController                                      │   │
│  │ │     └─ doGet() → getSettlements/Summary()                     │   │
│  │ │                                                               │   │
│  │ └─ @WebServlet("/api/group")                                    │   │
│  │    └─ GroupController                                           │   │
│  │       ├─ doGet() → getGroups/Members()                          │   │
│  │       └─ doPost() → createGroup/addMember()                     │   │
│  │                                                                  │   │
│  │ RESPONSIBILITIES:                                               │   │
│  │ • Extract HTTP parameters                                       │   │
│  │ • Call appropriate service                                      │   │
│  │ • Convert responses to JSON                                     │   │
│  │ • Handle HTTP status codes                                      │   │
│  └────────────────────┬──────────────────────────────────────────┘   │
│                       │ service.method()                             │
│                       │                                              │
│  ┌────────────────────▼──────────────────────────────────────────┐   │
│  │ SERVICE LAYER (Business Logic)                               │   │
│  │                                                              │   │
│  │ ├─ ExpenseService                                           │   │
│  │ │  ├─ addExpenseEqualSplit()                               │   │
│  │ │  ├─ addExpenseCustomSplit()                              │   │
│  │ │  └─ getExpensesByGroup()                                 │   │
│  │ │                                                          │   │
│  │ ├─ PaymentService                                          │   │
│  │ │  └─ recordPayment()                                      │   │
│  │ │                                                          │   │
│  │ ├─ SettlementService                                       │   │
│  │ │  ├─ getSettlementSummary()                               │   │
│  │ │  ├─ getPendingSettlements()                              │   │
│  │ │  └─ simplifyDebts()                                      │   │
│  │ │                                                          │   │
│  │ └─ GroupService                                            │   │
│  │    ├─ createGroup()                                        │   │
│  │    ├─ addMember()                                          │   │
│  │    └─ getGroupMembers()                                    │   │
│  │                                                            │   │
│  │ RESPONSIBILITIES:                                          │   │
│  │ • Implement business logic                                 │   │
│  │ • Validate business rules                                  │   │
│  │ • Coordinate multiple DAOs                                 │   │
│  │ • Perform calculations (splits, balances)                 │   │
│  │ • Return domain objects                                    │   │
│  └────────────────────┬─────────────────────────────────────┘   │
│                       │ dao.insert()/find()/update()             │
│                       │                                          │
│  ┌────────────────────▼─────────────────────────────────────┐   │
│  │ DATA ACCESS LAYER (DAOs)                                │   │
│  │                                                          │   │
│  │ ├─ ExpenseDAOImpl (implements ExpenseDAO)               │   │
│  │ │  ├─ insert(expense)                                   │   │
│  │ │  ├─ update(expense)                                   │   │
│  │ │  ├─ delete(id)                                        │   │
│  │ │  ├─ findById(id)                                      │   │
│  │ │  ├─ findByGroup(groupId)                              │   │
│  │ │  └─ findByUser(userId)                                │   │
│  │ │                                                        │   │
│  │ ├─ SettlementDAOImpl (implements SettlementDAO)         │   │
│  │ │  ├─ insert(settlement)                                │   │
│  │ │  ├─ update(settlement)                                │   │
│  │ │  ├─ findByGroup(groupId)                              │   │
│  │ │  └─ findPendingByGroup(groupId)                        │   │
│  │ │                                                        │   │
│  │ ├─ TransactionDAOImpl (implements TransactionDAO)       │   │
│  │ │  ├─ insert(transaction)                               │   │
│  │ │  ├─ findByUser(userId)                                │   │
│  │ │  └─ findByGroup(groupId)                              │   │
│  │ │                                                        │   │
│  │ ├─ GroupDAOImpl (implements GroupDAO)                   │   │
│  │ │  ├─ insert(group)                                     │   │
│  │ │  ├─ findById(id)                                      │   │
│  │ │  └─ findAll()                                         │   │
│  │ │                                                        │   │
│  │ └─ UserDAOImpl (implements UserDAO)                     │   │
│  │    ├─ insert(user)                                      │   │
│  │    └─ findByPhone(phone)                                │   │
│  │                                                          │   │
│  │ RESPONSIBILITIES:                                        │   │
│  │ • Create PreparedStatement with SQL                      │   │
│  │ • Set parameters (prevent SQL injection)                 │   │
│  │ • Execute queries                                        │   │
│  │ • Map ResultSet to objects                               │   │
│  │ • Handle SQLException                                    │   │
│  │ • Return results                                         │   │
│  └────────────────────┬──────────────────────────────────┘   │
│                       │ JDBC Connection                        │
│                       │ SQL: INSERT/SELECT/UPDATE              │
└───────────────────────┼────────────────────────────────────────┘
                        │
        ┌───────────────▼────────────────┐
        │  DATABASE CONNECTION LAYER      │
        │                                │
        │  DBUtil.getConnection()         │
        │  └─ Uses DatabaseConfig         │
        │     ├─ Reads db.properties      │
        │     └─ Creates JDBC Connection  │
        └───────────────┬────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────────┐
│                    MYSQL DATABASE LAYER                          │
│                                                                  │
│  Database: expense_tracker_db                                    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ TABLES:                                                 │   │
│  │                                                         │   │
│  │ ├─ expenses (Core Expenses)                            │   │
│  │ │  └─ Fields: expense_id, group_id, paid_by, amount    │   │
│  │ │            description, split_type, timestamps       │   │
│  │ │                                                       │   │
│  │ ├─ expense_participants (Split Details)               │   │
│  │ │  └─ Fields: participant_id, expense_id, user_id      │   │
│  │ │            share_amount, timestamps                  │   │
│  │ │                                                       │   │
│  │ ├─ settlements (Pending Payments)                      │   │
│  │ │  └─ Fields: settlement_id, group_id, from_user       │   │
│  │ │            to_user, net_balance, paid_amount         │   │
│  │ │            status (PENDING/SETTLED), timestamps      │   │
│  │ │                                                       │   │
│  │ ├─ transactions (Payment History)                      │   │
│  │ │  └─ Fields: transaction_id, from_user, to_user       │   │
│  │ │            amount, expense_id, note, timestamp       │   │
│  │ │                                                       │   │
│  │ ├─ groups (Friend Groups)                              │   │
│  │ │  └─ Fields: group_id, name, description              │   │
│  │ │            created_by, timestamp                     │   │
│  │ │                                                       │   │
│  │ ├─ group_members (Membership)                          │   │
│  │ │  └─ Fields: member_id, group_id, user_id, timestamp  │   │
│  │ │                                                       │   │
│  │ └─ users (User Information)                            │   │
│  │    └─ Fields: phone_number (PK), name, email, timestamp│   │
│  │                                                         │   │
│  │ RELATIONSHIPS:                                          │   │
│  │ • expenses.group_id → groups.group_id                  │   │
│  │ • expenses.paid_by → users.phone_number                │   │
│  │ • expense_participants → expenses & users               │   │
│  │ • settlements → groups & users                          │   │
│  │ • transactions → users                                  │   │
│  │ • group_members → groups & users                        │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│  OPERATIONS:                                                    │
│  • Executes INSERT for new data                               │
│  • Executes SELECT for retrieving data                         │
│  • Executes UPDATE for modifications                           │
│  • Executes DELETE for removal                                 │
│  • Maintains referential integrity                             │
│  • Persists all data to disk                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Data Flow Diagram: Adding an Expense

```
USER INTERACTION:
┌─────────────────────────────────────────────────────────────┐
│ User opens add-expense.html                                 │
│ Fills form:                                                 │
│ - Group: Friends                                            │
│ - Paid By: John                                             │
│ - Amount: 1200                                              │
│ - Description: Dinner                                       │
│ - Participants: John, Alice, Bob                            │
│ Clicks "Add Expense"                                        │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
JAVASCRIPT PROCESSING:
┌─────────────────────────────────────────────────────────────┐
│ JavaScript: submitExpense()                                 │
│ - Validates form                                            │
│ - Creates FormData object                                   │
│ - fetch POST /api/expense with data                        │
└────────────────────────┬────────────────────────────────────┘
                         │
                    HTTP Request
              POST /expense-tracker/api/expense
              Content-Type: application/x-www-form-urlencoded
              Body: groupId=1&paidBy=9876543210&...
                         │
                         ▼
SERVLET PROCESSING:
┌─────────────────────────────────────────────────────────────┐
│ ExpenseController.doPost(request, response)                 │
│ - Receive HTTP request                                      │
│ - Extract parameters:                                       │
│   groupId = 1                                               │
│   paidBy = "9876543210"                                     │
│   amount = 1200                                             │
│   participants = ["9876543210", "9876543211", "9876543212"]│
│ - Create ExpenseService instance                            │
│ - Call: expenseService.addExpenseEqualSplit(...)            │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
SERVICE PROCESSING:
┌─────────────────────────────────────────────────────────────┐
│ ExpenseService.addExpenseEqualSplit()                       │
│                                                             │
│ 1. Create Expense object (memory)                          │
│    expense = new Expense(1, "9876543210", 1200, ...)       │
│                                                             │
│ 2. Insert into database                                    │
│    expenseDAO.insert(expense)                              │
│                                                             │
│ 3. Calculate share                                         │
│    share = 1200 / 3 = 400                                  │
│                                                             │
│ 4. For each participant:                                   │
│    - participantDAO.insert(participant)                    │
│    - updateOrCreateSettlement(...)                         │
│                                                             │
│ 5. Return populated expense                                │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
DAO PROCESSING:
┌─────────────────────────────────────────────────────────────┐
│ ExpenseDAOImpl.insert(expense)                               │
│                                                             │
│ 1. Get database connection                                 │
│    conn = DBUtil.getConnection()                           │
│                                                             │
│ 2. Create PreparedStatement                                │
│    INSERT INTO expenses (group_id, paid_by, amount, ...)   │
│    VALUES (?, ?, ?, ?, ?)                                  │
│                                                             │
│ 3. Set parameters                                          │
│    pstmt.setInt(1, 1)                                      │
│    pstmt.setString(2, "9876543210")                        │
│    pstmt.setBigDecimal(3, 1200)                            │
│                                                             │
│ 4. Execute                                                 │
│    int rows = pstmt.executeUpdate()                        │
│                                                             │
│ 5. Get generated ID                                        │
│    return generatedKeys.getInt(1)  // 1                    │
│                                                             │
│ ExpenseParticipantDAOImpl.insert() → 3 times (similar)      │
│ SettlementDAOImpl.insert() → 2 times (Alice & Bob owe)      │
└────────────────────────┬────────────────────────────────────┘
                         │
                    JDBC Connection
              jdbc:mysql://localhost:3306/...
                         │
                         ▼
DATABASE OPERATIONS:
┌─────────────────────────────────────────────────────────────┐
│ MySQL Database (expense_tracker_db)                         │
│                                                             │
│ SQL 1: INSERT INTO expenses                                │
│   VALUES (1, 1, '9876543210', 1200, 'Dinner', 'EQUAL')    │
│   → expense_id = 1 (auto-generated)                        │
│                                                             │
│ SQL 2: INSERT INTO expense_participants (3x)               │
│   VALUES (1, 1, '9876543210', 400)                         │
│   VALUES (1, 2, '9876543211', 400)                         │
│   VALUES (1, 3, '9876543212', 400)                         │
│                                                             │
│ SQL 3: INSERT INTO settlements (2x)                        │
│   VALUES (1, 1, '9876543211', '9876543210', 400, 'PENDING')│
│   VALUES (2, 1, '9876543212', '9876543210', 400, 'PENDING')│
│                                                             │
│ Data committed to disk ✓                                    │
└────────────────────────┬────────────────────────────────────┘
                         │
                    ResultSet/Confirmation
                         │
                         ▼
RESPONSE PROCESSING:
┌─────────────────────────────────────────────────────────────┐
│ Data flows back up:                                         │
│                                                             │
│ DAO returns: expenseId = 1                                  │
│ Service returns: Expense object                             │
│ Controller converts to JSON                                 │
│ Returns HTTP 201 Created:                                  │
│                                                             │
│ {                                                           │
│   "success": true,                                          │
│   "expense": {                                              │
│     "expenseId": 1,                                         │
│     "amount": 1200,                                         │
│     ...                                                     │
│   }                                                         │
│ }                                                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                    HTTP Response
                         │
                         ▼
FRONTEND UPDATE:
┌─────────────────────────────────────────────────────────────┐
│ JavaScript receives response                                │
│ Parses JSON                                                 │
│ Shows success message                                       │
│ Redirects to dashboard                                      │
│                                                             │
│ Dashboard page loads:                                       │
│ - Calls GET /api/expense?groupId=1                         │
│ - Displays "Dinner ₹1200" in the list                       │
│ - Data retrieved from DATABASE                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Request-Response Lifecycle

```
CYCLE 1: CLIENT → SERVER → DATABASE → SERVER → CLIENT

┌──────────────────────────────────────────────────────────────┐
│ CLIENT BROWSER                                               │
│                                                              │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ add-expense.html (Webpage)                              │ │
│ │ User fills form and clicks "Submit"                     │ │
│ └──────────────────────┬──────────────────────────────────┘ │
│                        │                                    │
│ ┌──────────────────────▼──────────────────────────────────┐ │
│ │ JavaScript Code                                         │ │
│ │                                                         │ │
│ │ submitExpense() {                                       │ │
│ │   fetch('/api/expense', {                              │ │
│ │     method: 'POST',                                     │ │
│ │     body: new URLSearchParams({                         │ │
│ │       groupId: 1,                                       │ │
│ │       paidBy: '9876543210',                             │ │
│ │       amount: 1200,                                     │ │
│ │       description: 'Dinner',                            │ │
│ │       participants: [...],                              │ │
│ │       ...                                               │ │
│ │     })                                                  │ │
│ │   })                                                    │ │
│ │   .then(res => res.json())                              │ │
│ │   .then(data => {                                       │ │
│ │     if (data.success) {                                 │ │
│ │       alert('Success!');                                │ │
│ │       window.location = 'index.html';                   │ │
│ │     }                                                   │ │
│ │   })                                                    │ │
│ │ }                                                       │ │
│ │                                                         │ │
│ │ REQUEST → Network → HTTP POST                          │ │
│ └──────────────────────┬──────────────────────────────────┘ │
│                        │                                    │
│                        │ Content-Type: application/x-...   │
│                        │ Body: form data                   │
└────────────────────────┼────────────────────────────────────┘
                         │
           ┌─────────────▼─────────────┐
           │  HTTP REQUEST OVER NETWORK │
           │ (Internet / Localhost)     │
           │                            │
           │ POST /api/expense          │
           │ groupId=1&paidBy=...&...   │
           └─────────────┬─────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│ SERVER (Tomcat Application)                                 │
│                                                             │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ ExpenseController.doPost()                          │   │
│ │                                                     │   │
│ │ @Override                                          │   │
│ │ protected void doPost(HttpServletRequest req,      │   │
│ │                       HttpServletResponse res) {    │   │
│ │   // 1. Extract parameters                         │   │
│ │   int groupId = parseInt(req.getParameter(...))   │   │
│ │                                                     │   │
│ │   // 2. Call service                               │   │
│ │   Expense exp = expenseService                     │   │
│ │     .addExpenseEqualSplit(groupId, ...)           │   │
│ │                                                     │   │
│ │   // 3. Create response                            │   │
│ │   res.setContentType("application/json")           │   │
│ │   res.getWriter().print(gson.toJson(...))          │   │
│ │ }                                                   │   │
│ └──────────────────────┬───────────────────────────┘   │
│                        │                                 │
│ ┌──────────────────────▼───────────────────────────┐   │
│ │ ExpenseService.addExpenseEqualSplit()           │   │
│ │ - Create object                                 │   │
│ │ - Call DAOs                                     │   │
│ │ - Return result                                 │   │
│ └──────────────────────┬───────────────────────────┘   │
│                        │                                 │
│ ┌──────────────────────▼───────────────────────────┐   │
│ │ ExpenseDAOImpl.insert()                          │   │
│ │ - Create PreparedStatement                      │   │
│ │ - Execute SQL                                   │   │
│ │ - Return ID                                     │   │
│ └──────────────────────┬───────────────────────────┘   │
└────────────────────────┼────────────────────────────────┘
                         │
           ┌─────────────▼──────────────┐
           │  JDBC Connection to MySQL  │
           │ (TCP/IP over Network/Local)│
           │                            │
           │ INSERT ... VALUES ...      │
           │ SELECT ... WHERE ...       │
           │ UPDATE ... SET ...         │
           └─────────────┬──────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│ DATABASE (MySQL - expense_tracker_db)                       │
│                                                             │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ SQL ENGINE                                          │   │
│ │                                                     │   │
│ │ QUERY 1:                                            │   │
│ │ INSERT INTO expenses (group_id, paid_by, ...)      │   │
│ │ VALUES (1, '9876543210', 1200, ...)               │   │
│ │ → Validated, inserted, ID generated = 1            │   │
│ │                                                     │   │
│ │ QUERY 2:                                            │   │
│ │ INSERT INTO expense_participants (...)             │   │
│ │ VALUES (1, 1, '9876543210', 400)                  │   │
│ │ ... (2 more)                                        │   │
│ │ → Validated, inserted                              │   │
│ │                                                     │   │
│ │ QUERY 3:                                            │   │
│ │ INSERT INTO settlements (...)                      │   │
│ │ VALUES (1, 1, '9876543211', '9876543210', ...)    │   │
│ │ ... (1 more)                                        │   │
│ │ → Validated, inserted                              │   │
│ │                                                     │   │
│ │ All changes COMMITTED to disk ✓                    │   │
│ └──────────────────────┬───────────────────────────┘   │
└────────────────────────┼────────────────────────────────┘
                         │
           ┌─────────────▼──────────────┐
           │ JDBC ResultSet             │
           │ Rows affected: 1, 3, 2     │
           │ Generated Keys: 1          │
           └─────────────┬──────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│ SERVER (Tomcat Application) - Response Created             │
│                                                             │
│ Data flows back up layers:                                  │
│ DAO → Service → Controller → JSON → HTTP Response          │
│                                                             │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ HTTP RESPONSE                                        │   │
│ │                                                     │   │
│ │ Status: 201 Created                                │   │
│ │ Content-Type: application/json                     │   │
│ │                                                     │   │
│ │ Body:                                               │   │
│ │ {                                                   │   │
│ │   "success": true,                                 │   │
│ │   "expense": {                                      │   │
│ │     "expenseId": 1,                                │   │
│ │     "groupId": 1,                                  │   │
│ │     "paidBy": "9876543210",                        │   │
│ │     "amount": 1200,                                │   │
│ │     "description": "Dinner"                        │   │
│ │   },                                                │   │
│ │   "message": "Expense added successfully"          │   │
│ │ }                                                   │   │
│ └──────────────────────┬───────────────────────────┘   │
└────────────────────────┼────────────────────────────────┘
                         │
           ┌─────────────▼──────────────┐
           │  HTTP RESPONSE OVER NETWORK │
           │                            │
           │ Status: 201 Created        │
           │ JSON Body (response above)  │
           └─────────────┬──────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│ CLIENT BROWSER - Response Received                          │
│                                                             │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ JavaScript .then() Handler                          │   │
│ │                                                     │   │
│ │ .then(res => res.json())                           │   │
│ │ Parse response JSON                                │   │
│ │                                                     │   │
│ │ .then(data => {                                    │   │
│ │   if (data.success) {                              │   │
│ │     // Show success message                        │   │
│ │     alert('Expense added successfully!');          │   │
│ │                                                     │   │
│ │     // Redirect to dashboard                       │   │
│ │     window.location.href = 'index.html';           │   │
│ │   }                                                 │   │
│ │ })                                                  │   │
│ └──────────────────────┬───────────────────────────┘   │
│                        │                                 │
│ ┌──────────────────────▼───────────────────────────┐   │
│ │ Dashboard Loads (index.html)                     │   │
│ │                                                 │   │
│ │ JavaScript calls:                               │   │
│ │ fetch('/api/expense?groupId=1')                │   │
│ │                                                 │   │
│ │ Server queries database:                        │   │
│ │ SELECT * FROM expenses WHERE group_id = 1      │   │
│ │                                                 │   │
│ │ Returns expense we just added:                  │   │
│ │ ┌──────────────────────────────────────────┐   │   │
│ │ │ Expense ID: 1                             │   │   │
│ │ │ Group: Friends                            │   │   │
│ │ │ Paid By: John                             │   │   │
│ │ │ Amount: ₹1200                             │   │   │
│ │ │ Description: Dinner                       │   │   │
│ │ │ Split: ₹400 each (3 ways)                │   │   │
│ │ └──────────────────────────────────────────┘   │   │
│ │                                                 │   │
│ │ User sees expense in UI ✓                       │   │
│ └──────────────────────────────────────────────┘   │
│                                                     │
└─────────────────────────────────────────────────────┘

DATA PERSISTENCE PROOF:
→ Even if user closes browser
→ Even if server restarts
→ Even if server crashes

Data is PERMANENTLY in MySQL database
Next time user accesses:
  expense still there ✓
  participants still there ✓
  settlements still there ✓

ALL DATA IS DATABASE-BACKED!
```

---

## Database Schema Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE SCHEMA                             │
│                   expense_tracker_db (MySQL)                     │
│                                                                  │
│  ┌──────────────┐                   ┌──────────────┐            │
│  │    groups    │                   │    users     │            │
│  ├──────────────┤                   ├──────────────┤            │
│  │ group_id (PK)│◄─────created_by───│ phone_number │            │
│  │ name         │        (FK)        │ (PK)         │            │
│  │ description  │                   │ name         │            │
│  │ created_by   │                   │ email        │            │
│  │ created_at   │                   │ created_at   │            │
│  └──────┬───────┘                   └──────▲───────┘            │
│         │                                  │                    │
│         │ (1:N)                           │ (1:N)               │
│         │                                  │                    │
│         ▼                                  │                    │
│  ┌──────────────────┐            ┌────────┴────────────┐       │
│  │  group_members   │            │ settlements        │        │
│  ├──────────────────┤            ├────────────────────┤       │
│  │ member_id (PK)   │            │ settlement_id (PK) │        │
│  │ group_id (FK)◄───┼────────────│ group_id (FK)      │        │
│  │ user_id (FK)─────┼──┐         │ from_user (FK)─────┼──┐     │
│  │ joined_at        │  │         │ to_user (FK)───────┼──┼─┐   │
│  └──────────────────┘  │         │ net_balance        │  │ │   │
│                        │         │ paid_amount        │  │ │   │
│                        │         │ status             │  │ │   │
│  ┌──────────────────┐  │         │ created_at         │  │ │   │
│  │    expenses      │  │         │ updated_at         │  │ │   │
│  ├──────────────────┤  │         └────────────────────┘  │ │   │
│  │ expense_id (PK)  │  │                                 │ │   │
│  │ group_id (FK)────┼──────────────────┐                 │ │   │
│  │ paid_by (FK)─────┼──┘                │                 │ │   │
│  │ amount           │                  │                 │ │   │
│  │ description      │                  │                 │ │   │
│  │ split_type       │                  │                 │ │   │
│  │ created_at       │                  │                 │ │   │
│  └──────┬───────────┘                  │                 │ │   │
│         │ (1:N)                        │                 │ │   │
│         │                              │                 │ │   │
│         ▼                              │                 │ │   │
│  ┌──────────────────────┐              │                 │ │   │
│  │ expense_participants │              │                 │ │   │
│  ├──────────────────────┤              │                 │ │   │
│  │ participant_id (PK)  │              │                 │ │   │
│  │ expense_id (FK)      │              │                 │ │   │
│  │ user_id (FK)◄────────┼──────────────┼─────────────────┼─┼─┐ │
│  │ share_amount         │              │                 │ │ │ │
│  │ created_at           │              │                 │ │ │ │
│  └──────────────────────┘              │                 │ │ │ │
│                                        │                 │ │ │ │
│  ┌──────────────────┐                  │                 │ │ │ │
│  │  transactions    │                  │                 │ │ │ │
│  ├──────────────────┤                  │                 │ │ │ │
│  │ transaction_id   │                  │                 │ │ │ │
│  │ from_user (FK)───┼──────────────────┼─────────────────┘ │ │ │
│  │ to_user (FK)─────┼──────────────────┼─────────────────┐ │ │
│  │ amount           │                  │                 │ │ │
│  │ expense_id (FK)──┼──────────────────┘                 │ │ │
│  │ note             │                                    │ │ │
│  │ created_at       │                                    │ │ │
│  └──────────────────┘                                    │ │ │
│                                                          │ │ │
│  FOREIGN KEY RELATIONSHIPS (→ means FK points to PK):  │ │ │
│  ─────────────────────────────────────────────────────  │ │ │
│  • groups.created_by → users.phone_number              │ │ │
│  • group_members.group_id → groups.group_id             │ │ │
│  • group_members.user_id → users.phone_number           ◄─┘ │
│  • expenses.group_id → groups.group_id                  │   │
│  • expenses.paid_by → users.phone_number                ◄───┘
│  • expense_participants.expense_id → expenses.expense_id│
│  • expense_participants.user_id → users.phone_number    ◄─┐
│  • settlements.group_id → groups.group_id               │ │
│  • settlements.from_user → users.phone_number           ◄─┤
│  • settlements.to_user → users.phone_number             ◄─┤
│  • transactions.from_user → users.phone_number          ◄─┤
│  • transactions.to_user → users.phone_number            ◄─┘
│  • transactions.expense_id → expenses.expense_id        │
│                                                          │
│  DATA FLOW EXAMPLE (Adding ₹1200 Dinner Expense):      │
│  ─────────────────────────────────────────────────────  │
│                                                          │
│  1. User "John" (9876543210) creates expense           │
│  2. Insert into expenses:                               │
│     group_id=1, paid_by='9876543210', amount=1200       │
│  3. Insert into expense_participants (3 rows):          │
│     For John: user_id='9876543210', share=400           │
│     For Alice: user_id='9876543211', share=400          │
│     For Bob: user_id='9876543212', share=400            │
│  4. Insert into settlements (2 rows):                   │
│     Alice owes: from='9876543211', to='9876543210'      │
│     Bob owes: from='9876543212', to='9876543210'        │
│                                                          │
│  All data NORMALIZED and PERSISTED ✓                    │
└──────────────────────────────────────────────────────────┘
```

---

This completes the visual documentation of your fully DBMS-integrated expense tracker application!

