-- ============================================================
-- DATABASE VIEWS    ______ OPTIONAL
-- ============================================================
-- Views provide pre-computed summaries for common queries
-- They demonstrate SQL abstraction and simplify application logic
-- ============================================================

-- ============================================================
-- VIEW: v_group_expense_summary
-- ============================================================
-- Purpose: Shows total expenses and expense count per group
-- Uses: GROUP BY, aggregate functions (SUM, COUNT)
-- ============================================================
CREATE OR REPLACE VIEW v_group_expense_summary AS
SELECT 
    g.group_id,
    g.name AS group_name,
    COUNT(e.expense_id) AS total_expenses,
    COALESCE(SUM(e.amount), 0) AS total_amount,
    COUNT(DISTINCT e.paid_by) AS unique_payers
FROM groups g
LEFT JOIN expenses e ON g.group_id = e.group_id
GROUP BY g.group_id, g.name;

-- ============================================================
-- VIEW: v_pending_settlements
-- ============================================================
-- Purpose: Shows all pending and partial settlements
-- Useful for "who owes whom" display
-- ============================================================
CREATE OR REPLACE VIEW v_pending_settlements AS
SELECT 
    s.id AS settlement_id,
    g.name AS group_name,
    u1.name AS debtor_name,
    u2.name AS creditor_name,
    s.net_balance,
    s.status,
    s.last_updated
FROM settlements s
JOIN groups g ON s.group_id = g.group_id
JOIN users u1 ON s.from_user = u1.phone_number
JOIN users u2 ON s.to_user = u2.phone_number
WHERE s.status IN ('PENDING', 'PARTIAL')
ORDER BY s.net_balance DESC;

-- ============================================================
-- VIEW: v_user_balance_summary
-- ============================================================
-- Purpose: Per-user summary of total owed and total receivable
-- Uses: Subqueries, aggregate functions
-- ============================================================
CREATE OR REPLACE VIEW v_user_balance_summary AS
SELECT 
    u.phone_number AS user_id,
    u.name,
    COALESCE(owed.total_owed, 0) AS total_owed,
    COALESCE(receivable.total_receivable, 0) AS total_receivable,
    COALESCE(receivable.total_receivable, 0) - COALESCE(owed.total_owed, 0) AS net_balance
FROM users u
LEFT JOIN (
    -- Subquery: Total amount user owes to others
    SELECT from_user, SUM(net_balance) AS total_owed
    FROM settlements
    WHERE status IN ('PENDING', 'PARTIAL')
    GROUP BY from_user
) owed ON u.phone_number = owed.from_user
LEFT JOIN (
    -- Subquery: Total amount others owe to user
    SELECT to_user, SUM(net_balance) AS total_receivable
    FROM settlements
    WHERE status IN ('PENDING', 'PARTIAL')
    GROUP BY to_user
) receivable ON u.phone_number = receivable.to_user;

-- ============================================================
-- VIEW: v_expense_details
-- ============================================================
-- Purpose: Complete expense information with payer name
-- Joins multiple tables for comprehensive display
-- ============================================================
CREATE OR REPLACE VIEW v_expense_details AS
SELECT 
    e.expense_id,
    g.name AS group_name,
    e.description,
    e.amount,
    u.name AS paid_by_name,
    e.split_type,
    e.created_at,
    (SELECT COUNT(*) FROM expense_participants ep WHERE ep.expense_id = e.expense_id) AS participant_count
FROM expenses e
JOIN groups g ON e.group_id = g.group_id
JOIN users u ON e.paid_by = u.phone_number
ORDER BY e.created_at DESC;

-- ============================================================
-- VIEW: v_member_expenses
-- ============================================================
-- Purpose: Shows each member's share in expenses
-- Useful for individual expense tracking
-- ============================================================
CREATE OR REPLACE VIEW v_member_expenses AS
SELECT 
    g.name AS group_name,
    u.name AS member_name,
    e.description AS expense_description,
    ep.share_amount,
    CASE WHEN ep.is_settled THEN 'Settled' ELSE 'Pending' END AS settlement_status,
    e.created_at
FROM expense_participants ep
JOIN expenses e ON ep.expense_id = e.expense_id
JOIN groups g ON e.group_id = g.group_id
JOIN users u ON ep.user_id = u.phone_number
ORDER BY g.name, u.name, e.created_at DESC;

-- ============================================================
-- VIEW: v_transaction_history
-- ============================================================
-- Purpose: Complete transaction history with user names
-- ============================================================
CREATE OR REPLACE VIEW v_transaction_history AS
SELECT 
    t.transaction_id,
    u1.name AS from_user_name,
    u2.name AS to_user_name,
    t.amount,
    t.note,
    e.description AS expense_description,
    t.created_at
FROM transactions t
JOIN users u1 ON t.from_user = u1.phone_number
JOIN users u2 ON t.to_user = u2.phone_number
LEFT JOIN expenses e ON t.expense_id = e.expense_id
ORDER BY t.created_at DESC;

-- ============================================================
-- END OF VIEWS
-- ============================================================
