-- ============================================================
-- DEMO QUERIES FOR DBMS VIVA  OPTIONAL
-- ============================================================
-- This file contains queries demonstrating various SQL concepts:
-- - JOINs (INNER, LEFT, multiple tables)
-- - Aggregate Functions (SUM, COUNT, AVG)
-- - GROUP BY and HAVING clauses
-- - Subqueries (scalar, correlated)
-- - CASE expressions
-- - Date functions
-- ============================================================

-- ============================================================
-- QUERY 1: Total expenses per group with member count
-- Demonstrates: JOIN, GROUP BY, aggregate functions
-- ============================================================
-- ============================================================
-- QUERY 1: Total expenses per group with member count
-- Demonstrates: JOIN, GROUP BY, aggregate functions
-- ============================================================
SELECT 
    g.name AS group_name,
    COUNT(DISTINCT gm.user_id) AS member_count,
    COUNT(DISTINCT e.expense_id) AS expense_count,
    COALESCE(SUM(e.amount), 0) AS total_expenses
FROM groups g
LEFT JOIN group_members gm ON g.group_id = gm.group_id
LEFT JOIN expenses e ON g.group_id = e.group_id
GROUP BY g.group_id, g.name;

-- ============================================================
-- QUERY 2: Find groups with expenses exceeding a threshold
-- Demonstrates: HAVING clause with aggregate condition
-- ============================================================
SELECT 
    g.name AS group_name,
    SUM(e.amount) AS total_expenses
FROM groups g
JOIN expenses e ON g.group_id = e.group_id
GROUP BY g.group_id, g.name
HAVING SUM(e.amount) > 5000;

-- ============================================================
-- QUERY 3: User expense statistics
-- Demonstrates: Multiple JOINs, CASE, aggregate functions
-- ============================================================
SELECT 
    u.name,
    COUNT(DISTINCT CASE WHEN e.paid_by = u.phone_number THEN e.expense_id END) AS expenses_paid,
    COALESCE(SUM(CASE WHEN e.paid_by = u.phone_number THEN e.amount END), 0) AS total_paid,
    COUNT(DISTINCT ep.expense_id) AS expenses_participated,
    COALESCE(SUM(ep.share_amount), 0) AS total_share
FROM users u
LEFT JOIN expenses e ON e.paid_by = u.phone_number
LEFT JOIN expense_participants ep ON ep.user_id = u.phone_number
GROUP BY u.phone_number, u.name;

-- ============================================================
-- QUERY 4: Who owes whom summary (net balance calculation)
-- Demonstrates: Subqueries, COALESCE, arithmetic operations
-- ============================================================
SELECT 
    debtor.name AS debtor,
    creditor.name AS creditor,
    s.net_balance AS amount_owed,
    s.status
FROM settlements s
JOIN users debtor ON s.from_user = debtor.phone_number
JOIN users creditor ON s.to_user = creditor.phone_number
WHERE s.status != 'SETTLED'
ORDER BY s.net_balance DESC;

-- ============================================================
-- QUERY 5: Members who owe more than average
-- Demonstrates: Correlated subquery, comparison with aggregate
-- ============================================================
SELECT 
    u.name,
    total_owed.amount AS total_owed
FROM users u
JOIN (
    SELECT from_user, SUM(net_balance) AS amount
    FROM settlements
    WHERE status IN ('PENDING', 'PARTIAL')
    GROUP BY from_user
) total_owed ON u.phone_number = total_owed.from_user
WHERE total_owed.amount > (
    SELECT AVG(net_balance)
    FROM settlements
    WHERE status IN ('PENDING', 'PARTIAL')
);

-- ============================================================
-- QUERY 6: Monthly expense trend
-- Demonstrates: Date functions, GROUP BY with date parts
-- ============================================================
SELECT 
    DATE_FORMAT(created_at, '%Y-%m') AS month,
    COUNT(*) AS expense_count,
    SUM(amount) AS total_amount,
    AVG(amount) AS average_expense
FROM expenses
GROUP BY DATE_FORMAT(created_at, '%Y-%m')
ORDER BY month DESC;

-- ============================================================
-- QUERY 7: Top expense payers per group
-- Demonstrates: Window functions simulation, ranking
-- ============================================================
SELECT 
    g.name AS group_name,
    u.name AS top_payer,
    paid_amounts.total_paid
FROM (
    SELECT 
        group_id,
        paid_by,
        SUM(amount) AS total_paid,
        ROW_NUMBER() OVER (PARTITION BY group_id ORDER BY SUM(amount) DESC) AS rn
    FROM expenses
    GROUP BY group_id, paid_by
) paid_amounts
JOIN groups g ON paid_amounts.group_id = g.group_id
JOIN users u ON paid_amounts.paid_by = u.phone_number
WHERE paid_amounts.rn = 1;

-- ============================================================
-- QUERY 8: Expense split verification
-- Demonstrates: Ensuring data integrity with aggregate check
-- ============================================================
SELECT 
    e.expense_id,
    e.description,
    e.amount AS expense_amount,
    SUM(ep.share_amount) AS total_shares,
    CASE 
        WHEN ABS(e.amount - SUM(ep.share_amount)) < 0.01 THEN 'VALID'
        ELSE 'INVALID'
    END AS validation_status
FROM expenses e
LEFT JOIN expense_participants ep ON e.expense_id = ep.expense_id
GROUP BY e.expense_id, e.description, e.amount;

-- ============================================================
-- QUERY 9: Transaction history with running balance
-- Demonstrates: Self-join pattern for running totals
-- ============================================================
SELECT 
    t1.transaction_id,
    payer.name AS from_user,
    receiver.name AS to_user,
    t1.amount,
    t1.created_at,
    (
        SELECT SUM(t2.amount)
        FROM transactions t2
        WHERE t2.from_user = t1.from_user
          AND t2.to_user = t1.to_user
          AND t2.created_at <= t1.created_at
    ) AS cumulative_paid
FROM transactions t1
JOIN users payer ON t1.from_user = payer.phone_number
JOIN users receiver ON t1.to_user = receiver.phone_number
ORDER BY t1.from_user, t1.to_user, t1.created_at;

-- ============================================================
-- QUERY 10: TRANSACTION DEMONSTRATION (ACID Properties)
-- ============================================================
-- This demonstrates how to use transactions for atomic operations

-- Start transaction
START TRANSACTION;

-- Step 1: Insert payment record
INSERT INTO transactions (from_user, to_user, amount, expense_id, note)
VALUES ('9876543213', '9876543210', 500.00, 1, 'Partial payment for hotel');

-- Step 2: Update settlement balance
UPDATE settlements
SET net_balance = net_balance - 500.00,
    status = CASE 
        WHEN net_balance - 500.00 <= 0 THEN 'SETTLED'
        ELSE 'PARTIAL'
    END
WHERE from_user = '9876543213' AND to_user = '9876543210' AND group_id = 1;

-- If everything is successful, commit
COMMIT;

-- If there's an error, rollback would be:
-- ROLLBACK;

-- ============================================================
-- QUERY 11: Users with no pending settlements (fully settled)
-- Demonstrates: NOT EXISTS subquery
-- ============================================================
SELECT u.name
FROM users u
WHERE NOT EXISTS (
    SELECT 1 
    FROM settlements s 
    WHERE s.from_user = u.phone_number 
      AND s.status IN ('PENDING', 'PARTIAL')
);

-- ============================================================
-- QUERY 12: Group-wise settlement summary
-- Demonstrates: Complex aggregation with multiple conditions
-- ============================================================
SELECT 
    g.name AS group_name,
    COUNT(CASE WHEN s.status = 'PENDING' THEN 1 END) AS pending_count,
    COUNT(CASE WHEN s.status = 'PARTIAL' THEN 1 END) AS partial_count,
    COUNT(CASE WHEN s.status = 'SETTLED' THEN 1 END) AS settled_count,
    SUM(CASE WHEN s.status IN ('PENDING', 'PARTIAL') THEN s.net_balance ELSE 0 END) AS outstanding_amount
FROM groups g
LEFT JOIN settlements s ON g.group_id = s.group_id
GROUP BY g.group_id, g.name;

-- ============================================================
-- END OF DEMO QUERIES
-- ============================================================
