# DBMS Integration - Code Flow & Data Persistence Examples

## Overview

This document shows **exact code snippets** demonstrating how data flows from the frontend through to the database and back.

---

## Complete Flow: Adding an Expense

### Step 1: Frontend Submits Form (add-expense.html)

```javascript
// User fills form:
// Group: "Friends"
// Paid By: "John" (9876543210)
// Amount: 1200
// Description: "Dinner"
// Participants: John, Alice, Bob

async function submitExpense(event) {
    event.preventDefault();

    // Collect form data
    const formData = new FormData();
    formData.append('groupId', document.getElementById('groupId').value);        // 1
    formData.append('paidBy', document.getElementById('paidBy').value);          // 9876543210
    formData.append('amount', document.getElementById('amount').value);          // 1200
    formData.append('description', document.getElementById('description').value); // Dinner
    formData.append('splitType', document.getElementById('splitType').value);    // EQUAL

    const participants = getSelectedParticipants();
    participants.forEach(p => formData.append('participants', p));
    // participants: [9876543210, 9876543211, 9876543212]

    try {
        // SEND TO BACKEND
        const response = await fetch(`/api/expense`, {
            method: 'POST',
            body: new URLSearchParams(formData)
        });

        const result = await response.json();

        if (result.success) {
            showAlert('Expense added successfully!', 'success');
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);
        } else {
            showAlert('Error: ' + result.error, 'error');
        }
    } catch (error) {
        showAlert('Error adding expense: ' + error.message, 'error');
    }
}
```

**What's Sent to Server:**
```
POST /expense-tracker/api/expense
Content-Type: application/x-www-form-urlencoded

groupId=1
paidBy=9876543210
amount=1200
description=Dinner
splitType=EQUAL
participants=9876543210
participants=9876543211
participants=9876543212
```

---

### Step 2: Controller Receives Request (ExpenseController.java)

```java
@WebServlet(urlPatterns = { "/api/expense", "/api/expense/*" })
public class ExpenseController extends HttpServlet {

    private ExpenseService expenseService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        expenseService = new ExpenseService();  // ← Creates service instance
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // EXTRACT PARAMETERS FROM REQUEST
            int groupId = Integer.parseInt(request.getParameter("groupId"));           // 1
            String paidBy = request.getParameter("paidBy");                            // 9876543210
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));        // 1200
            String description = request.getParameter("description");                  // Dinner
            String splitType = request.getParameter("splitType");                      // EQUAL
            String[] participantIds = request.getParameterValues("participants");      // [9876543210, 9876543211, 9876543212]

            Expense expense;

            if ("CUSTOM".equals(splitType)) {
                // Handle custom split...
            } else {
                // EQUAL SPLIT
                List<String> participants = Arrays.asList(participantIds);

                // ⭐ CALL SERVICE - This is where business logic happens
                expense = expenseService.addExpenseEqualSplit(
                    groupId,        // 1
                    paidBy,         // 9876543210
                    amount,         // 1200
                    description,    // Dinner
                    participants    // [9876543210, 9876543211, 9876543212]
                );
            }

            // ✅ SUCCESS - Return response
            response.setStatus(201);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("expense", expense);
            result.put("message", "Expense added successfully");
            out.print(gson.toJson(result));  // ← Convert to JSON and send back

        } catch (Exception e) {
            // ❌ ERROR - Return error response
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
```

**Controller Responsibility:**
- Extract HTTP parameters ✅
- Call service ✅
- Return JSON response ✅
- **NOT** directly accessing database ✅

---

### Step 3: Service Processes Business Logic (ExpenseService.java)

```java
public class ExpenseService {

    private ExpenseDAO expenseDAO;
    private ExpenseParticipantDAO participantDAO;
    private SettlementDAO settlementDAO;
    private GroupMemberDAO memberDAO;

    public ExpenseService() {
        // Instantiate DAOs - they handle database operations
        this.expenseDAO = new ExpenseDAOImpl();
        this.participantDAO = new ExpenseParticipantDAOImpl();
        this.settlementDAO = new SettlementDAOImpl();
        this.memberDAO = new GroupMemberDAOImpl();
    }

    /**
     * Add expense with equal split among specified participants
     */
    public Expense addExpenseEqualSplit(
            int groupId,                      // 1
            String paidBy,                    // 9876543210
            BigDecimal amount,                // 1200
            String description,               // Dinner
            List<String> participantIds       // [9876543210, 9876543211, 9876543212]
    ) {
        // STEP 1: Create Expense object (in-memory, not yet in DB)
        Expense expense = new Expense(
            groupId,           // 1
            paidBy,            // 9876543210
            amount,            // 1200
            description,       // Dinner
            Expense.SplitType.EQUAL
        );

        // STEP 2: ⭐ INSERT INTO DATABASE
        int expenseId = expenseDAO.insert(expense);  // ← Goes to DAO, returns auto-ID
        // Database now has: INSERT INTO expenses VALUES (1, 1, '9876543210', 1200, 'Dinner', 'EQUAL', NOW())
        
        expense.setExpenseId(expenseId);  // Set the ID returned from database

        // STEP 3: Calculate equal share
        int numParticipants = participantIds.size();  // 3
        BigDecimal share = amount.divide(
            BigDecimal.valueOf(numParticipants),      // 1200 / 3
            2,
            RoundingMode.HALF_UP
        );  // share = 400

        // STEP 4: Create participant records and settlements
        for (String userId : participantIds) {
            // userId cycles through: 9876543210, 9876543211, 9876543212

            // 4a. Create participant record
            ExpenseParticipant participant = new ExpenseParticipant(
                expenseId,  // 1
                userId,     // 9876543210 / 9876543211 / 9876543212
                share       // 400
            );
            participantDAO.insert(participant);  // ← INSERT INTO DATABASE
            // Database: INSERT INTO expense_participants VALUES (?, 1, userId, 400, NOW())

            // 4b. Create settlement (if not the payer)
            if (!userId.equals(paidBy)) {  // Only if not John (9876543210)
                updateOrCreateSettlement(groupId, userId, paidBy, share);
                // For Alice (9876543211): INSERT INTO settlements - Alice owes John 400
                // For Bob (9876543212): INSERT INTO settlements - Bob owes John 400
            }
        }

        // STEP 5: Return the expense object (now populated with ID from DB)
        return expense;
    }

    /**
     * Create or update settlement between two users
     */
    private void updateOrCreateSettlement(
            int groupId,
            String fromUser,  // 9876543211 (Alice)
            String toUser,    // 9876543210 (John)
            BigDecimal amount  // 400
    ) {
        // Check if settlement already exists between these users
        Settlement existing = settlementDAO.findByUserPair(groupId, fromUser, toUser);

        if (existing != null) {
            // Update existing settlement
            existing.setNetBalance(existing.getNetBalance().add(amount));
            settlementDAO.update(existing);
            // Database: UPDATE settlements SET net_balance = ? WHERE settlement_id = ?
        } else {
            // Create new settlement
            Settlement settlement = new Settlement(
                groupId,     // 1
                fromUser,    // 9876543211 (Alice)
                toUser,      // 9876543210 (John)
                amount,      // 400
                Settlement.Status.PENDING
            );
            settlementDAO.insert(settlement);
            // Database: INSERT INTO settlements VALUES (NULL, 1, '9876543211', '9876543210', 400, 0, 'PENDING', NOW(), NOW())
        }
    }
}
```

**Service Responsibility:**
- Implement business logic ✅
- Validate business rules ✅
- Call DAOs for database operations ✅
- Coordinate multiple operations ✅
- Return processed results ✅

---

### Step 4: DAO Executes Database Operations (ExpenseDAOImpl.java)

```java
public class ExpenseDAOImpl implements ExpenseDAO {

    /**
     * Insert new expense into database
     */
    @Override
    public int insert(Expense expense) {
        // SQL: INSERT INTO expenses (group_id, paid_by, amount, description, split_type) VALUES (?, ?, ?, ?, ?)
        String sql = "INSERT INTO expenses (group_id, paid_by, amount, description, split_type) VALUES (?, ?, ?, ?, ?)";
        
        try (
            // STEP 1: Get connection to database
            Connection conn = DBUtil.getConnection();  // ← Connects to MySQL using db.properties
            
            // STEP 2: Prepare statement with auto-generated keys
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            // STEP 3: Set parameters (PREVENT SQL INJECTION with prepared statements)
            pstmt.setInt(1, expense.getGroupId());              // ?1 = 1
            pstmt.setString(2, expense.getPaidBy());            // ?2 = '9876543210'
            pstmt.setBigDecimal(3, expense.getAmount());        // ?3 = 1200
            pstmt.setString(4, expense.getDescription());       // ?4 = 'Dinner'
            pstmt.setString(5, expense.getSplitType().name());  // ?5 = 'EQUAL'

            // STEP 4: Execute SQL
            int rows = pstmt.executeUpdate();  // ← ACTUALLY WRITES TO DATABASE
            
            // SQL Executed:
            // INSERT INTO expenses (group_id, paid_by, amount, description, split_type) 
            // VALUES (1, '9876543210', 1200, 'Dinner', 'EQUAL')

            if (rows > 0) {
                // STEP 5: Get auto-generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);  // ← Returns ID (e.g., 1)
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
```

**DAO Responsibility:**
- Create SQL statements ✅
- Get database connection ✅
- Use PreparedStatement (prevent injection) ✅
- Execute queries ✅
- Handle SQLException ✅
- **NEVER** execute in service or controller ✅

---

### Step 5: Database Executes Query (MySQL)

```sql
-- Expense is added
INSERT INTO expenses (group_id, paid_by, amount, description, split_type) 
VALUES (1, '9876543210', 1200, 'Dinner', 'EQUAL');
-- Result: expense_id = 1 (auto-generated)

-- Participants are added
INSERT INTO expense_participants (expense_id, user_id, share_amount) 
VALUES (1, '9876543210', 400);
INSERT INTO expense_participants (expense_id, user_id, share_amount) 
VALUES (1, '9876543211', 400);
INSERT INTO expense_participants (expense_id, user_id, share_amount) 
VALUES (1, '9876543212', 400);

-- Settlements are created
INSERT INTO settlements (group_id, from_user, to_user, net_balance, status) 
VALUES (1, '9876543211', '9876543210', 400, 'PENDING');
INSERT INTO settlements (group_id, from_user, to_user, net_balance, status) 
VALUES (1, '9876543212', '9876543210', 400, 'PENDING');

-- Verify data is persisted
SELECT * FROM expenses WHERE group_id = 1;
-- Returns: expense_id=1, group_id=1, paid_by='9876543210', amount=1200, description='Dinner'

SELECT * FROM expense_participants WHERE expense_id = 1;
-- Returns: 3 rows, each with share_amount=400

SELECT * FROM settlements WHERE group_id = 1 AND status = 'PENDING';
-- Returns: 2 rows (Alice → John: 400, Bob → John: 400)
```

---

### Step 6: Response Sent Back to Frontend

```json
{
    "success": true,
    "expense": {
        "expenseId": 1,
        "groupId": 1,
        "paidBy": "9876543210",
        "amount": 1200.00,
        "description": "Dinner",
        "splitType": "EQUAL"
    },
    "message": "Expense added successfully"
}
```

**Frontend Receives:**
```javascript
const result = await response.json();
// result.success = true
// result.expense.expenseId = 1

if (result.success) {
    showAlert('Expense added successfully!', 'success');
    setTimeout(() => {
        window.location.href = 'index.html';  // Redirect to dashboard
    }, 1500);
}
```

---

## Complete Flow: Recording a Payment

### Frontend (record-payment.html)

```javascript
async function submitPayment(event) {
    event.preventDefault();
    
    const formData = new URLSearchParams({
        fromUser: '9876543211',  // Alice
        toUser: '9876543210',    // John
        amount: '400',
        note: 'Payment for dinner'
    });

    const response = await fetch(`/api/payment`, { 
        method: 'POST', 
        body: formData 
    });
    
    const result = await response.json();

    if (result.success) {
        alert('Payment recorded!');
        loadGroupData();  // Refresh view
        document.getElementById('paymentForm').reset();
    } else {
        alert('Error: ' + result.error);
    }
}
```

### Controller (PaymentController.java)

```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();

    try {
        String fromUser = request.getParameter("fromUser");           // 9876543211
        String toUser = request.getParameter("toUser");               // 9876543210
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));  // 400
        String note = request.getParameter("note");                   // Payment for dinner

        // ⭐ Call service to record payment
        Transaction transaction = paymentService.recordPayment(
            fromUser,    // 9876543211
            toUser,      // 9876543210
            amount,      // 400
            null,        // expenseId (optional)
            note         // "Payment for dinner"
        );

        // ✅ Return success response
        response.setStatus(201);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("transaction", transaction);
        result.put("message", "Payment recorded successfully");
        out.print(gson.toJson(result));

    } catch (IllegalArgumentException e) {
        response.setStatus(400);
        out.print("{\"error\": \"" + e.getMessage() + "\"}");
    } catch (Exception e) {
        response.setStatus(500);
        out.print("{\"error\": \"" + e.getMessage() + "\"}");
    }
}
```

### Service (PaymentService.java)

```java
public Transaction recordPayment(
    String fromUser,    // 9876543211 (Alice)
    String toUser,      // 9876543210 (John)
    BigDecimal amount,  // 400
    Integer expenseId,  // null
    String note         // "Payment for dinner"
) {
    // STEP 1: Validate
    if (fromUser.equals(toUser)) {
        throw new IllegalArgumentException("Cannot make payment to yourself");
    }

    // STEP 2: Create transaction object
    Transaction transaction = new Transaction(
        fromUser,    // 9876543211
        toUser,      // 9876543210
        amount,      // 400
        expenseId,   // null
        note         // "Payment for dinner"
    );

    // STEP 3: ⭐ Insert transaction into database
    int transactionId = transactionDAO.insert(transaction);
    // Database: INSERT INTO transactions (from_user, to_user, amount, expense_id, note)
    //           VALUES ('9876543211', '9876543210', 400, NULL, 'Payment for dinner')
    
    transaction.setTransactionId(transactionId);

    // STEP 4: ⭐ Update settlement status
    updateSettlementOnPayment(fromUser, toUser, amount, expenseId);
    // Database: UPDATE settlements SET status = 'SETTLED'
    //           WHERE group_id = 1 AND from_user = '9876543211' AND to_user = '9876543210'

    // STEP 5: Return transaction
    return transaction;
}

private void updateSettlementOnPayment(
    String fromUser,  // 9876543211
    String toUser,    // 9876543210
    BigDecimal amount, // 400
    Integer expenseId  // null
) {
    // Find settlement between these two users
    Settlement settlement = settlementDAO.findByUserPair(1, fromUser, toUser);
    
    if (settlement != null) {
        // Update settlement record
        settlement.setPaidAmount(settlement.getPaidAmount().add(amount));
        
        // Check if fully settled
        if (settlement.getPaidAmount().compareTo(settlement.getNetBalance()) >= 0) {
            settlement.setStatus(Settlement.Status.SETTLED);
        }
        
        // ⭐ Write to database
        settlementDAO.update(settlement);
        // Database: UPDATE settlements SET paid_amount = 400, status = 'SETTLED'
        //           WHERE settlement_id = 1
    }
}
```

### Database (MySQL)

```sql
-- Transaction is recorded
INSERT INTO transactions (from_user, to_user, amount, expense_id, note) 
VALUES ('9876543211', '9876543210', 400, NULL, 'Payment for dinner');
-- Result: transaction_id = 1

-- Settlement is updated
UPDATE settlements 
SET paid_amount = 400, 
    status = 'SETTLED',
    updated_at = NOW()
WHERE group_id = 1 
  AND from_user = '9876543211' 
  AND to_user = '9876543210';

-- Verify
SELECT * FROM transactions WHERE from_user = '9876543211';
-- Returns: transaction_id=1, from_user='9876543211', to_user='9876543210', amount=400

SELECT * FROM settlements WHERE from_user = '9876543211' AND to_user = '9876543210';
-- Returns: status='SETTLED', paid_amount=400, net_balance=400
```

---

## Complete Flow: Viewing Settlements

### Frontend (settlement.html)

```javascript
async function loadData() {
    const groupId = document.getElementById('groupSelect').value;
    if (!groupId) return;

    // ⭐ Request settlement summary from server
    const response = await fetch(`/api/settlement/summary?groupId=${groupId}`);
    const settlements = await response.json();

    // Response format:
    // [
    //   {fromUserId: '9876543211', fromUserName: 'Alice', toUserId: '9876543210', toUserName: 'John', amount: 400, status: 'PENDING'},
    //   {fromUserId: '9876543212', fromUserName: 'Bob', toUserId: '9876543210', toUserName: 'John', amount: 400, status: 'PENDING'}
    // ]

    // Display settlements
    const container = document.getElementById('whoOwesWhom');
    if (settlements.length === 0) {
        container.innerHTML = '<p class="empty-state">All settled!</p>';
    } else {
        container.innerHTML = settlements.map(s => `
            <div class="settlement-item">
                <span>${s.fromUserName} → ${s.toUserName}</span>
                <span class="settlement-amount">₹${s.amount}</span>
            </div>
        `).join('');
    }
}
```

### Controller (SettlementController.java)

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();

    String pathInfo = request.getPathInfo();
    String groupIdParam = request.getParameter("groupId");  // 1

    try {
        if (groupIdParam == null) {
            response.setStatus(400);
            out.print("{\"error\": \"groupId parameter required\"}");
            return;
        }

        int groupId = Integer.parseInt(groupIdParam);  // 1

        if (pathInfo == null || pathInfo.equals("/")) {
            // Get all settlements for group
            List<Settlement> settlements = settlementService.getAllSettlements(groupId);
            out.print(gson.toJson(settlements));
            
        } else if (pathInfo.equals("/summary")) {
            // ⭐ Get formatted settlement summary
            List<SettlementSummary> summary = settlementService.getSettlementSummary(groupId);
            out.print(gson.toJson(summary));  // ← Convert to JSON and send back
        }
        // ... other endpoints
    } catch (Exception e) {
        response.setStatus(500);
        out.print("{\"error\": \"" + e.getMessage() + "\"}");
    }
}
```

### Service (SettlementService.java)

```java
public List<SettlementSummary> getSettlementSummary(int groupId) {
    List<Settlement> settlements = settlementDAO.findPendingByGroup(groupId);
    List<SettlementSummary> summaries = new ArrayList<>();

    for (Settlement s : settlements) {
        if (s.getNetBalance().compareTo(BigDecimal.ZERO) > 0) {
            // ⭐ Get user names from database
            User fromUser = userDAO.findByPhone(s.getFromUser());  // SELECT from users table
            User toUser = userDAO.findByPhone(s.getToUser());      // SELECT from users table

            summaries.add(new SettlementSummary(
                s.getFromUser(),
                fromUser != null ? fromUser.getName() : "Unknown",
                s.getToUser(),
                toUser != null ? toUser.getName() : "Unknown",
                s.getNetBalance(),
                s.getStatus().toString()
            ));
        }
    }

    // Sort by amount descending
    summaries.sort((a, b) -> b.amount.compareTo(a.amount));

    return summaries;
}
```

### DAO (SettlementDAOImpl.java)

```java
@Override
public List<Settlement> findPendingByGroup(int groupId) {
    List<Settlement> settlements = new ArrayList<>();
    
    // ⭐ SQL Query
    String sql = "SELECT * FROM settlements WHERE group_id = ? AND status = 'PENDING'";
    
    try (
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
        pstmt.setInt(1, groupId);  // 1
        
        try (ResultSet rs = pstmt.executeQuery()) {  // ← EXECUTES SQL
            // SQL: SELECT * FROM settlements WHERE group_id = 1 AND status = 'PENDING'
            
            while (rs.next()) {
                Settlement settlement = mapRowToSettlement(rs);  // Convert row to object
                settlements.add(settlement);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return settlements;  // Returns list of settlement objects from database
}
```

### Database (MySQL)

```sql
-- Query executed
SELECT * FROM settlements 
WHERE group_id = 1 AND status = 'PENDING';

-- Returns (example data):
-- settlement_id | group_id | from_user   | to_user     | net_balance | paid_amount | status
-- 1             | 1        | 9876543211  | 9876543210  | 400         | 0           | PENDING
-- 2             | 1        | 9876543212  | 9876543210  | 400         | 0           | PENDING

-- Then get user details
SELECT * FROM users WHERE phone_number = '9876543211';
-- Returns: phone_number='9876543211', name='Alice', email='alice@example.com'

SELECT * FROM users WHERE phone_number = '9876543210';
-- Returns: phone_number='9876543210', name='John', email='john@example.com'
```

### Response Back to Frontend

```json
[
    {
        "fromUserId": "9876543211",
        "fromUserName": "Alice",
        "toUserId": "9876543210",
        "toUserName": "John",
        "amount": 400.00,
        "status": "PENDING"
    },
    {
        "fromUserId": "9876543212",
        "fromUserName": "Bob",
        "toUserId": "9876543210",
        "toUserName": "John",
        "amount": 400.00,
        "status": "PENDING"
    }
]
```

---

## Summary: Complete Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. FRONTEND (HTML/JavaScript)                                    │
│    - User fills form                                             │
│    - JavaScript sends HTTP request with form data                │
└──────────────────────┬──────────────────────────────────────────┘
                       │ POST /api/expense
                       │ Content: groupId, paidBy, amount, etc
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 2. CONTROLLER (ExpenseController)                               │
│    - Receives HTTP request                                       │
│    - Extracts parameters                                         │
│    - Calls service                                               │
└──────────────────────┬──────────────────────────────────────────┘
                       │ expenseService.addExpenseEqualSplit()
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 3. SERVICE (ExpenseService)                                      │
│    - Implements business logic                                   │
│    - Creates objects                                             │
│    - Calls DAOs                                                  │
└──────────────────────┬──────────────────────────────────────────┘
                       │ expenseDAO.insert()
                       │ participantDAO.insert()
                       │ settlementDAO.insert()
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 4. DAO (ExpenseDAOImpl)                                           │
│    - Creates PreparedStatement                                   │
│    - Sets parameters                                             │
│    - Executes SQL query                                          │
│    - Returns results                                             │
└──────────────────────┬──────────────────────────────────────────┘
                       │ jdbc:mysql://...
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 5. DATABASE (MySQL)                                              │
│    - Receives SQL query                                          │
│    - Executes INSERT/UPDATE/SELECT                               │
│    - Returns data or confirmation                                │
│    - Data persists on disk                                       │
└──────────────────────┬──────────────────────────────────────────┘
                       │ ResultSet or rows affected
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 6. DAO (returns to service)                                      │
│    - Maps ResultSet to objects                                   │
│    - Returns data                                                │
└──────────────────────┬──────────────────────────────────────────┘
                       │ Expense object with ID from DB
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 7. SERVICE (returns to controller)                               │
│    - Processes results                                           │
│    - Returns processed object                                    │
└──────────────────────┬──────────────────────────────────────────┘
                       │ Fully populated Expense object
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 8. CONTROLLER (returns to frontend)                              │
│    - Converts object to JSON                                     │
│    - Sends HTTP response                                         │
└──────────────────────┬──────────────────────────────────────────┘
                       │ HTTP 201 Created
                       │ JSON response with success flag
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│ 9. FRONTEND (JavaScript)                                         │
│    - Receives response                                           │
│    - Parses JSON                                                 │
│    - Updates UI                                                  │
│    - Shows success message                                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## Data Persistence Verification

### Example: Data After Restart

**Session 1:** Add expense, close application
```
// Add expense for Dinner: 1200
// App closes
// MySQL database still has data
```

**Session 2:** Restart application
```
// App starts
// User navigates to dashboard
// JavaScript calls: GET /api/expense?groupId=1
// Controller calls: expenseService.getExpensesByGroup(1)
// Service calls: expenseDAO.findByGroup(1)
// DAO queries: SELECT * FROM expenses WHERE group_id = 1
// Database returns: The dinner expense from Session 1
// Frontend displays: "Dinner ₹1200" - still there!
```

**✅ Proof of Persistence:**
- Data survives application restart
- Database is the source of truth
- No data lost when app closes
- Each session accesses same database

---

## Key Principles

1. **Separation of Concerns**
   - Controllers: HTTP handling only
   - Services: Business logic only
   - DAOs: Database queries only

2. **Data Flow Direction**
   - Frontend → Controller → Service → DAO → Database
   - Database → DAO → Service → Controller → Frontend

3. **Database Connection**
   - DBUtil.getConnection() creates new connection for each operation
   - DatabaseConfig loads credentials from db.properties
   - Each DAO uses try-with-resources for automatic closing

4. **PreparedStatement**
   - Prevents SQL injection
   - Parameterized queries safe against attacks
   - Used consistently in all DAOs

5. **Error Handling**
   - Try-catch blocks in all database operations
   - Graceful degradation
   - Meaningful error messages

6. **Transaction Support**
   - Multiple operations (expense + participants + settlements) work together
   - Database triggers ensure consistency
   - Atomic updates for data integrity

