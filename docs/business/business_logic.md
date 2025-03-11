# Business Logic Documentation

## Introduction
This document outlines the business logic for managing customer debts and processing payments within the clean water plant management system. It includes detailed descriptions of SQL functions and procedures used to handle billing, payments, debt allocation, and debt history management.

## Detailed Logic Descriptions

### 1. Creating New Bills
- **Function**: `create_new_bill(customer_id, meter_id, reading_id, water_volume, unit_price, billing_period_start, billing_period_end, due_date, created_by)`
- **Purpose**: Issues new bills, calculates total amounts, checks debt limits, and updates customer debt.
- **Usage Example**:
```sql
CREATE OR REPLACE FUNCTION create_new_bill(
    p_customer_id INTEGER,
    p_meter_id INTEGER,
    p_reading_id INTEGER,
    p_water_volume DECIMAL(10, 2),
    p_unit_price DECIMAL(10, 2),
    p_billing_period_start DATE,
    p_billing_period_end DATE,
    p_due_date DATE,
    p_created_by INTEGER
) RETURNS INTEGER AS $$
DECLARE
    v_bill_id INTEGER;
    v_total_amount DECIMAL(10, 2);
    v_bill_number VARCHAR(50);
    v_current_debt DECIMAL(15, 2);
    v_credit_limit DECIMAL(15, 2);
    v_customer_code VARCHAR(50);
BEGIN
    -- Tính tổng số tiền hóa đơn
    v_total_amount := p_water_volume * p_unit_price;
    
    -- Tạo mã hóa đơn, ví dụ: BILL-2023-000001
    SELECT 'BILL-' || to_char(CURRENT_DATE, 'YYYY') || '-' || 
            lpad(nextval('bill_number_seq')::text, 6, '0')
    INTO v_bill_number;
    
    -- Lấy thông tin dư nợ và hạn mức của khách hàng
    SELECT customer_code, current_debt, credit_limit
    INTO v_customer_code, v_current_debt, v_credit_limit
    FROM customers
    WHERE id = p_customer_id;
    
    -- Kiểm tra xem sau khi thêm hóa đơn mới, dư nợ có vượt quá hạn mức không
    IF (v_current_debt + v_total_amount > v_credit_limit) THEN
        RAISE NOTICE 'Cảnh báo: Dư nợ của khách hàng % sẽ vượt quá hạn mức sau khi thêm hóa đơn này.', v_customer_code;
        RAISE NOTICE 'Dư nợ hiện tại: %, Hóa đơn mới: %, Hạn mức: %', v_current_debt, v_total_amount, v_credit_limit;
        RAISE NOTICE 'Số tiền tối thiểu cần thanh toán: %', (v_current_debt + v_total_amount - v_credit_limit);
    END IF;
    
    -- Tạo hóa đơn mới
    INSERT INTO bills (
        bill_number, customer_id, meter_id, reading_id, 
        water_volume, unit_price, total_amount, paid_amount, remaining_amount,
        billing_period_start, billing_period_end, due_date, 
        bill_status, payment_status, order_value_amount, created_by, created_date
    ) VALUES (
        v_bill_number, p_customer_id, p_meter_id, p_reading_id, 
        p_water_volume, p_unit_price, v_total_amount, 0, v_total_amount,
        p_billing_period_start, p_billing_period_end, p_due_date, 
        'issued', 'unpaid', v_total_amount, p_created_by, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_bill_id;
    
    -- Cập nhật dư nợ của khách hàng
    UPDATE customers
    SET current_debt = current_debt + v_total_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_customer_id;
    
    -- Ghi nhận vào lịch sử dư nợ
    INSERT INTO customer_debt_history (
        customer_id, previous_debt, new_debt, change_amount,
        change_type, reference_id, reference_type, description
    ) VALUES (
        p_customer_id, v_current_debt, v_current_debt + v_total_amount, v_total_amount,
        'bill_issued', v_bill_id, 'bill', 
        'Phát hành hóa đơn ' || v_bill_number || ' với số tiền ' || v_total_amount
    );
    
    RETURN v_bill_id;
END;
$$ LANGUAGE plpgsql;
```

### 2. Processing Customer Payments
- **Function**: `process_payment(customer_id, payment_amount, payment_date, payment_method, created_by)`
- **Purpose**: Processes payments, allocates amounts to outstanding bills, and updates customer debt.
- **Usage Example**:
```sql
CREATE OR REPLACE FUNCTION process_payment(
    p_customer_id INTEGER,
    p_amount DECIMAL(10, 2),
    p_payment_method VARCHAR(50),
    p_received_by INTEGER,
    p_details TEXT DEFAULT NULL
) RETURNS INTEGER AS $$
DECLARE
    v_payment_id INTEGER;
    v_payment_number VARCHAR(50);
    v_current_debt DECIMAL(15, 2);
    v_remaining_payment DECIMAL(10, 2);
    v_bill_record RECORD;
    v_allocated_amount DECIMAL(10, 2);
BEGIN
    -- Bắt đầu transaction
    BEGIN
    
    -- Tạo mã thanh toán, ví dụ: PMT-2023-000001
    SELECT 'PMT-' || to_char(CURRENT_DATE, 'YYYY') || '-' || 
            lpad(nextval('payment_number_seq')::text, 6, '0')
    INTO v_payment_number;
    
    -- Lấy dư nợ hiện tại của khách hàng
    SELECT current_debt INTO v_current_debt
    FROM customers
    WHERE id = p_customer_id;
    
    -- Nếu thanh toán nhiều hơn dư nợ, thông báo và giới hạn số tiền
    IF p_amount > v_current_debt THEN
        RAISE NOTICE 'Thanh toán (%) lớn hơn dư nợ hiện tại (%). Chỉ ghi nhận số tiền bằng dư nợ.', p_amount, v_current_debt;
        p_amount := v_current_debt;
    END IF;
    
    -- Tạo bản ghi thanh toán
    INSERT INTO payment_histories (
        payment_number, customer_id, amount, previous_debt, remaining_debt,
        payment_method, payment_status, received_by, details, payment_date
    ) VALUES (
        v_payment_number, p_customer_id, p_amount, v_current_debt, v_current_debt - p_amount,
        p_payment_method, 'completed', p_received_by, p_details, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_payment_id;
    
    -- Khởi tạo biến theo dõi số tiền còn lại cần phân bổ
    v_remaining_payment := p_amount;
    
    -- Phân bổ thanh toán cho các hóa đơn, ưu tiên hóa đơn cũ nhất (FIFO)
    FOR v_bill_record IN 
        SELECT id, bill_number, total_amount, paid_amount, remaining_amount 
        FROM bills
        WHERE customer_id = p_customer_id 
        AND payment_status IN ('unpaid', 'partially_paid') 
        ORDER BY billing_period_start ASC, due_date ASC, id ASC
    LOOP
        -- Nếu không còn tiền để phân bổ, thoát khỏi vòng lặp
        IF v_remaining_payment <= 0 THEN
            EXIT;
        END IF;
        
        -- Tính số tiền sẽ phân bổ cho hóa đơn này
        IF v_remaining_payment >= v_bill_record.remaining_amount THEN
            -- Nếu còn đủ tiền, thanh toán toàn bộ hóa đơn này
            v_allocated_amount := v_bill_record.remaining_amount;
            v_remaining_payment := v_remaining_payment - v_bill_record.remaining_amount;
        ELSE
            -- Nếu không đủ, thanh toán một phần
            v_allocated_amount := v_remaining_payment;
            v_remaining_payment := 0;
        END IF;
        
        -- Tạo bản ghi phân bổ thanh toán
        INSERT INTO payment_bill_allocations (
            payment_id, bill_id, allocated_amount, created_at
        ) VALUES (
            v_payment_id, v_bill_record.id, v_allocated_amount, CURRENT_TIMESTAMP
        );
        
        -- Cập nhật trạng thái hóa đơn
        UPDATE bills
        SET paid_amount = paid_amount + v_allocated_amount,
            remaining_amount = remaining_amount - v_allocated_amount,
            payment_status = CASE 
                                WHEN remaining_amount - v_allocated_amount <= 0 THEN 'paid'
                                ELSE 'partially_paid'
                             END,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = v_bill_record.id;
        
        RAISE NOTICE 'Phân bổ % cho hóa đơn % (Số tiền còn lại: %)', 
                    v_allocated_amount, v_bill_record.bill_number, v_remaining_payment;
    END LOOP;
    
    -- Nếu vẫn còn tiền chưa phân bổ, thông báo
    IF v_remaining_payment > 0 THEN
        RAISE NOTICE 'Còn % chưa được phân bổ do không có hóa đơn nào cần thanh toán.', v_remaining_payment;
    END IF;
    
    -- Cập nhật dư nợ của khách hàng
    UPDATE customers
    SET current_debt = current_debt - (p_amount - v_remaining_payment),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_customer_id;
    
    -- Ghi nhận vào lịch sử dư nợ
    INSERT INTO customer_debt_history (
        customer_id, previous_debt, new_debt, change_amount,
        change_type, reference_id, reference_type, description
    ) VALUES (
        p_customer_id, 
        v_current_debt, 
        v_current_debt - (p_amount - v_remaining_payment), 
        (p_amount - v_remaining_payment) * (-1),
        'payment', v_payment_id, 'payment', 
        'Thanh toán ' || v_payment_number || ' với số tiền ' || (p_amount - v_remaining_payment)
    );
    
    -- Kết thúc transaction
    COMMIT;
    
    RETURN v_payment_id;
    
    EXCEPTION WHEN OTHERS THEN
        -- Nếu có lỗi, rollback và ghi log
        ROLLBACK;
        RAISE EXCEPTION 'Lỗi xử lý thanh toán: %', SQLERRM;
    END;
END;
$$ LANGUAGE plpgsql;
```

### 3. Allocating Payments to Bills
- **Function**: `allocate_payment_to_bills(payment_id, allocations JSONB)`
- **Purpose**: Allocates payments to specific bills based on provided allocation data.
- **Usage Example**:
```sql
CREATE OR REPLACE FUNCTION allocate_payment_to_bills(
    p_payment_id INTEGER,
    p_allocation_data JSONB -- Mảng JSON chứa thông tin phân bổ tùy chỉnh
) RETURNS VOID AS $$
DECLARE
    v_payment RECORD;
    v_customer_id INTEGER;
    v_current_debt DECIMAL(15, 2);
    v_total_allocated DECIMAL(10, 2) := 0;
    v_allocation RECORD;
    v_bill RECORD;
BEGIN
    -- Bắt đầu transaction
    BEGIN
    
    -- Lấy thông tin thanh toán
    SELECT id, customer_id, amount, previous_debt, remaining_debt
    INTO v_payment
    FROM payment_histories
    WHERE id = p_payment_id;
    
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy thanh toán với ID %', p_payment_id;
    END IF;
    
    v_customer_id := v_payment.customer_id;
    
    -- Lấy dư nợ hiện tại của khách hàng
    SELECT current_debt INTO v_current_debt
    FROM customers
    WHERE id = v_customer_id;
    
    -- Xóa các phân bổ hiện có (nếu có)
    DELETE FROM payment_bill_allocations WHERE payment_id = p_payment_id;
    
    -- Lặp qua mỗi phân bổ trong dữ liệu JSON
    FOR v_allocation IN SELECT * FROM jsonb_to_recordset(p_allocation_data) AS x(bill_id INTEGER, amount DECIMAL(10, 2))
    LOOP
        -- Kiểm tra hóa đơn có tồn tại và thuộc về khách hàng
        SELECT id, remaining_amount + 
               (SELECT COALESCE(SUM(allocated_amount), 0) 
                FROM payment_bill_allocations 
                WHERE bill_id = v_allocation.bill_id)
        INTO v_bill
        FROM bills
        WHERE id = v_allocation.bill_id AND customer_id = v_customer_id;
        
        IF NOT FOUND THEN
            RAISE EXCEPTION 'Hóa đơn ID % không tồn tại hoặc không thuộc khách hàng này', v_allocation.bill_id;
        END IF;
        
        -- Kiểm tra số tiền phân bổ có hợp lệ không
        IF v_allocation.amount > v_bill.remaining_amount THEN
            RAISE EXCEPTION 'Số tiền phân bổ (%) cho hóa đơn ID % vượt quá số tiền còn lại (%)', 
                v_allocation.amount, v_allocation.bill_id, v_bill.remaining_amount;
        END IF;
        
        -- Tạo bản ghi phân bổ
        INSERT INTO payment_bill_allocations (
            payment_id, bill_id, allocated_amount, created_at
        ) VALUES (
            p_payment_id, v_allocation.bill_id, v_allocation.amount, CURRENT_TIMESTAMP
        );
        
        -- Cập nhật tổng số tiền đã phân bổ
        v_total_allocated := v_total_allocated + v_allocation.amount;
        
        -- Cập nhật trạng thái hóa đơn
        UPDATE bills
        SET paid_amount = paid_amount + v_allocation.amount,
            remaining_amount = remaining_amount - v_allocation.amount,
            payment_status = CASE 
                                WHEN remaining_amount - v_allocation.amount <= 0 THEN 'paid'
                                ELSE 'partially_paid'
                             END,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = v_allocation.bill_id;
    END LOOP;
    
    -- Kiểm tra tổng số tiền phân bổ có khớp với số tiền thanh toán không
    IF v_total_allocated != v_payment.amount THEN
        RAISE EXCEPTION 'Tổng số tiền phân bổ (%) không khớp với số tiền thanh toán (%)', 
            v_total_allocated, v_payment.amount;
    END IF;
    
    -- Cập nhật dư nợ khách hàng
    UPDATE customers
    SET current_debt = current_debt - v_total_allocated,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = v_customer_id;
    
    -- Cập nhật lịch sử dư nợ
    INSERT INTO customer_debt_history (
        customer_id, previous_debt, new_debt, change_amount,
        change_type, reference_id, reference_type, description
    ) VALUES (
        v_customer_id, 
        v_current_debt, 
        v_current_debt - v_total_allocated, 
        v_total_allocated * (-1),
        'payment_allocation', p_payment_id, 'payment', 
        'Phân bổ thanh toán ID ' || p_payment_id || ' với số tiền ' || v_total_allocated
    );
    
    -- Kết thúc transaction
    COMMIT;
    
    EXCEPTION WHEN OTHERS THEN
        -- Nếu có lỗi, rollback và ghi log
        ROLLBACK;
        RAISE EXCEPTION 'Lỗi phân bổ thanh toán: %', SQLERRM;
    END;
END;
$$ LANGUAGE plpgsql;
```

### 4. Checking and Managing Debt Limits
- **Function**: `check_customer_debt_limit(customer_id)`
- **Purpose**: Checks customer's current debt against their credit limit and returns debt details.
- **Usage Example**:
```sql
CREATE OR REPLACE FUNCTION check_customer_debt_limit(
    p_customer_id INTEGER
) RETURNS TABLE (
    customer_id INTEGER,
    customer_code VARCHAR(50),
    customer_name VARCHAR(255),
    current_debt DECIMAL(15, 2),
    credit_limit DECIMAL(15, 2),
    over_limit BOOLEAN,
    excess_amount DECIMAL(15, 2),
    oldest_unpaid_bill_id INTEGER,
    oldest_unpaid_bill_date DATE,
    oldest_unpaid_bill_amount DECIMAL(10, 2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.id AS customer_id,
        c.customer_code,
        i.full_name AS customer_name,
        c.current_debt,
        c.credit_limit,
        (c.current_debt > c.credit_limit) AS over_limit,
        GREATEST(c.current_debt - c.credit_limit, 0) AS excess_amount,
        b.id AS oldest_unpaid_bill_id,
        b.billing_period_start AS oldest_unpaid_bill_date,
        b.remaining_amount AS oldest_unpaid_bill_amount
    FROM customers c
    JOIN informations i ON c.information_id = i.id
    LEFT JOIN LATERAL (
        SELECT id, billing_period_start, remaining_amount
        FROM bills
        WHERE customer_id = c.id AND payment_status IN ('unpaid', 'partially_paid')
        ORDER BY billing_period_start ASC, id ASC
        LIMIT 1
    ) b ON true
    WHERE c.id = p_customer_id;
END;
$$ LANGUAGE plpgsql;
```

### 5. Report debt

```sql
CREATE OR REPLACE FUNCTION get_customer_debt_report(
    p_customer_id INTEGER
) RETURNS TABLE (
    bill_id INTEGER,
    bill_number VARCHAR(50),
    billing_period_start DATE,
    billing_period_end DATE,
    due_date DATE,
    total_amount DECIMAL(10, 2),
    paid_amount DECIMAL(10, 2),
    remaining_amount DECIMAL(10, 2),
    payment_status VARCHAR(50),
    is_overdue BOOLEAN,
    days_overdue INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        b.id AS bill_id,
        b.bill_number,
        b.billing_period_start,
        b.billing_period_end,
        b.due_date,
        b.total_amount,
        b.paid_amount,
        b.remaining_amount,
        b.payment_status,
        (CURRENT_DATE > b.due_date AND b.payment_status != 'paid') AS is_overdue,
        CASE 
            WHEN CURRENT_DATE > b.due_date AND b.payment_status != 'paid' 
            THEN CURRENT_DATE - b.due_date
            ELSE 0
        END AS days_overdue
    FROM bills b
    WHERE b.customer_id = p_customer_id
    ORDER BY b.billing_period_start ASC, b.id ASC;
END;
$$ LANGUAGE plpgsql;
```

### 6. Minium payment

```sql
CREATE OR REPLACE FUNCTION calculate_minimum_payment(
    p_customer_id INTEGER
) RETURNS DECIMAL(15, 2) AS $$
DECLARE
    v_current_debt DECIMAL(15, 2);
    v_credit_limit DECIMAL(15, 2);
    v_minimum_payment DECIMAL(15, 2);
BEGIN
    -- Lấy thông tin dư nợ và hạn mức của khách hàng
    SELECT current_debt, credit_limit
    INTO v_current_debt, v_credit_limit
    FROM customers
    WHERE id = p_customer_id;
    
    -- Tính số tiền tối thiểu cần thanh toán
    IF v_current_debt > v_credit_limit THEN
        v_minimum_payment := v_current_debt - v_credit_limit;
    ELSE
        v_minimum_payment := 0;
    END IF;
    
    RETURN v_minimum_payment;
END;
$$ LANGUAGE plpgsql;
```

### 7. Updating Debt History
- **Function**: Automatically handled within `create_new_bill` and `process_payment` functions.
- **Purpose**: Logs changes in customer debt over time for audit and tracking purposes.

## SQL Functions and Procedures
Detailed SQL implementations of these functions are provided in the `database.sql` file. Ensure to review the SQL file for exact implementation details and any updates.

## Important Notes
- Always verify customer debt limits before issuing new bills.
- Ensure accurate payment allocations to maintain data integrity.
- Regularly review debt history for auditing and customer service purposes. 