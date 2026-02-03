-- ============================================================
-- DATABASE TRIGGERS
-- ============================================================
-- Triggers automatically execute in response to data changes
-- They maintain data consistency without application intervention
-- 
-- ACID Properties demonstrated:
-- - Atomicity: Trigger executes as part of the same transaction
-- - Consistency: Ensures business rules are enforced
-- ============================================================

DELIMITER //

-- ============================================================
-- TRIGGER: trg_after_transaction_insert
-- ============================================================
-- Purpose: Automatically updates settlement balance when a 
--          payment transaction is recorded
-- Event: AFTER INSERT on transactions table
-- Action: Reduces the net_balance in settlements table
-- ============================================================
CREATE TRIGGER trg_after_transaction_insert
AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
    DECLARE v_group_id INT;
    DECLARE v_current_balance DECIMAL(10,2);
    
    -- Find the group for this transaction (via expense or direct)
    IF NEW.expense_id IS NOT NULL THEN
        SELECT group_id INTO v_group_id 
        FROM expenses 
        WHERE expense_id = NEW.expense_id;
    ELSE
        -- For direct payments, find any common group
        SELECT gm1.group_id INTO v_group_id
        FROM group_members gm1
        JOIN group_members gm2 ON gm1.group_id = gm2.group_id
        WHERE gm1.user_id = NEW.from_user 
          AND gm2.user_id = NEW.to_user
        LIMIT 1;
    END IF;
    
    -- Update the settlement balance
    UPDATE settlements
    SET net_balance = net_balance - NEW.amount,
        status = CASE 
            WHEN net_balance - NEW.amount <= 0 THEN 'SETTLED'
            WHEN net_balance - NEW.amount < net_balance THEN 'PARTIAL'
            ELSE status
        END
    WHERE group_id = v_group_id
      AND from_user = NEW.from_user
      AND to_user = NEW.to_user;
      
    -- Mark expense participant as settled if fully paid
    IF NEW.expense_id IS NOT NULL THEN
        UPDATE expense_participants
        SET is_settled = TRUE
        WHERE expense_id = NEW.expense_id
          AND user_id = NEW.from_user
          AND share_amount <= (
              SELECT COALESCE(SUM(amount), 0)
              FROM transactions
              WHERE expense_id = NEW.expense_id
                AND from_user = NEW.from_user
          );
    END IF;
END//

-- ============================================================
-- TRIGGER: trg_after_expense_insert
-- ============================================================
-- Purpose: Creates initial settlement records when expense is added
-- Event: AFTER INSERT on expense_participants
-- Action: Creates or updates settlement entries
-- ============================================================
CREATE TRIGGER trg_after_expense_participant_insert
AFTER INSERT ON expense_participants
FOR EACH ROW
BEGIN
    DECLARE v_group_id INT;
    DECLARE v_paid_by VARCHAR(20);
    
    -- Get expense details
    SELECT group_id, paid_by INTO v_group_id, v_paid_by
    FROM expenses
    WHERE expense_id = NEW.expense_id;
    
    -- Create settlement record if participant is not the payer
    IF NEW.user_id != v_paid_by THEN
        INSERT INTO settlements (group_id, from_user, to_user, net_balance, status)
        VALUES (v_group_id, NEW.user_id, v_paid_by, NEW.share_amount, 'PENDING')
        ON DUPLICATE KEY UPDATE 
            net_balance = net_balance + NEW.share_amount,
            status = 'PENDING';
    END IF;
END//

-- ============================================================
-- TRIGGER: trg_validate_expense_split
-- ============================================================
-- Purpose: Validates that participant shares sum to expense total
-- Event: BEFORE UPDATE on expenses (after all participants added)
-- Note: This is a simplified validation trigger
-- ============================================================
CREATE TRIGGER trg_before_expense_update
BEFORE UPDATE ON expenses
FOR EACH ROW
BEGIN
    DECLARE v_total_shares DECIMAL(10,2);
    
    -- Calculate sum of all participant shares
    SELECT COALESCE(SUM(share_amount), 0) INTO v_total_shares
    FROM expense_participants
    WHERE expense_id = NEW.expense_id;
    
    -- Validate shares sum matches expense amount (with small tolerance)
    IF ABS(v_total_shares - NEW.amount) > 0.01 AND v_total_shares > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Participant shares do not sum to expense amount';
    END IF;
END//

-- ============================================================
-- TRIGGER: trg_prevent_self_transaction
-- ============================================================
-- Purpose: Prevent users from creating transactions to themselves
-- Event: BEFORE INSERT on transactions
-- Note: This duplicates CHECK constraint for DB compatibility
-- ============================================================
CREATE TRIGGER trg_prevent_self_transaction
BEFORE INSERT ON transactions
FOR EACH ROW
BEGIN
    IF NEW.from_user = NEW.to_user THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot create transaction to yourself';
    END IF;
END//

DELIMITER ;

-- ============================================================
-- END OF TRIGGERS
-- ============================================================
-- SUMMARY:
-- 1. trg_after_transaction_insert: Auto-updates settlements on payment
-- 2. trg_after_expense_participant_insert: Creates settlement records
-- 3. trg_before_expense_update: Validates expense splits
-- 4. trg_prevent_self_transaction: Data integrity check
-- ============================================================
