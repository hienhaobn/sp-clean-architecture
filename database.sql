-- Database schema creation for water supply management system
-- Updated version with additional tables and columns

-- Create permissions table
CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Create roles table
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    attributes JSONB
);

-- Create role_permission table (many-to-many relationship)
CREATE TABLE role_permission (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id),
    UNIQUE (role_id, permission_id)
);

-- Create addresses table (with self-reference for hierarchy)
CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id INTEGER,
    type VARCHAR(50),
    city VARCHAR(100),
    district VARCHAR(100),
    ward VARCHAR(100),
    street VARCHAR(255),
    postal_code VARCHAR(20),
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES addresses(id)
);

-- Create informations table
CREATE TABLE informations (
    id SERIAL PRIMARY KEY,
    indentity_number VARCHAR(50),
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    address_id INTEGER,
    password VARCHAR(255),
    avatar_url TEXT,
    status VARCHAR(20) DEFAULT 'active',
    last_login TIMESTAMP,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    information_id INTEGER NOT NULL,
    role_id INTEGER,
    employee_id VARCHAR(50),
    department VARCHAR(100),
    position VARCHAR(100),
    hire_date DATE,
    employment_status VARCHAR(50),
    FOREIGN KEY (information_id) REFERENCES informations(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Create user_role table (many-to-many relationship)
CREATE TABLE user_role (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    UNIQUE (user_id, role_id)
);

-- Create customers table
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    customer_code VARCHAR(50) UNIQUE,
    information_id INTEGER NOT NULL,
    service_note TEXT,
    referral_code VARCHAR(50),
    customer_type VARCHAR(100), -- gia đình, doanh nghiệp, công nghiệp
    contract_number VARCHAR(100),
    contract_start_date DATE,
    contract_end_date DATE,
    connection_date DATE,
    is_active BOOLEAN DEFAULT true,
    credit_score INTEGER,
    current_debt DECIMAL(15, 2) DEFAULT 0, -- Dư nợ hiện tại
    credit_limit DECIMAL(15, 2) DEFAULT 1000000, -- Hạn mức dư nợ (mặc định 1 triệu)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (information_id) REFERENCES informations(id)
);

-- Create water_plants table
CREATE TABLE water_plants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,
    address_id INTEGER,
    capacity DECIMAL(15, 2), -- Công suất (m3/ngày)
    operational_status VARCHAR(50),
    establishment_date DATE,
    manager_id INTEGER, -- Liên kết với users
    technical_details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (manager_id) REFERENCES users(id)
);

-- Create distribution_zones table
CREATE TABLE distribution_zones (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,
    plant_id INTEGER, -- Nhà máy phục vụ
    area_size DECIMAL(10, 2), -- Diện tích (km2)
    population INTEGER, -- Dân số
    address_id INTEGER,
    description TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_id) REFERENCES water_plants(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Create meters table with additional fields
CREATE TABLE meters (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    serial_number VARCHAR(100) NOT NULL,
    status VARCHAR(20),
    address_id INTEGER,
    zone_id INTEGER, -- Zone meter belongs to
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    meter_type VARCHAR(50), -- Loại đồng hồ (cơ, điện tử, thông minh)
    manufacturer VARCHAR(100),
    model VARCHAR(100),
    max_flow_rate DECIMAL(10, 2),
    installed_date DATE,
    last_maintain_date DATE,
    next_maintain_date DATE,
    warranty_period DATE,
    calibration_date DATE,
    next_calibration_date DATE,
    reading_method VARCHAR(50), -- manual, automatic
    reading_unit VARCHAR(20) DEFAULT 'm3',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (zone_id) REFERENCES distribution_zones(id)
);

-- Create equipment table
CREATE TABLE equipment (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    equipment_type VARCHAR(100),
    plant_id INTEGER,
    model VARCHAR(255),
    serial_number VARCHAR(100),
    manufacturer VARCHAR(255),
    purchase_date DATE,
    warranty_expiry DATE,
    operational_status VARCHAR(50),
    last_maintenance_date DATE,
    next_maintenance_date DATE,
    specifications JSONB,
    location_details VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_id) REFERENCES water_plants(id)
);

-- Create maintenance_records table
CREATE TABLE maintenance_records (
    id SERIAL PRIMARY KEY,
    equipment_id INTEGER,
    meter_id INTEGER,
    maintenance_date TIMESTAMP,
    maintenance_type VARCHAR(100), -- Bảo trì định kỳ, sửa chữa, thay thế
    performed_by INTEGER, -- Liên kết với users
    cost DECIMAL(10, 2),
    parts_replaced TEXT,
    maintenance_details TEXT,
    next_maintenance_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (meter_id) REFERENCES meters(id),
    FOREIGN KEY (performed_by) REFERENCES users(id)
);

-- Create tasks table with expanded fields
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    "desc" TEXT,
    task_type VARCHAR(100), -- meter reading, maintenance, installation, etc.
    status VARCHAR(50),
    priority VARCHAR(20),
    assigned_to INTEGER,
    created_by INTEGER,
    customer_id INTEGER,
    meter_id INTEGER,
    equipment_id INTEGER,
    zone_id INTEGER,
    start_date TIMESTAMP,
    due_time TIMESTAMP,
    completed_date TIMESTAMP,
    data_detail JSONB,
    attachments TEXT[],
    recurring BOOLEAN DEFAULT false,
    recurrence_pattern VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_to) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (meter_id) REFERENCES meters(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (zone_id) REFERENCES distribution_zones(id)
);

-- Create read_histories table with expanded fields
CREATE TABLE read_histories (
    id SERIAL PRIMARY KEY,
    order_id INTEGER,
    task_id INTEGER,
    customer_id INTEGER NOT NULL,
    meter_id INTEGER NOT NULL,
    meter_reading DECIMAL(10, 2) NOT NULL, -- Current meter reading
    previous_value DECIMAL(10, 2), -- Previous meter reading
    consumption DECIMAL(10, 2), -- Calculated consumption
    reading_type VARCHAR(50), -- manual, automatic, estimated
    is_estimated BOOLEAN DEFAULT false,
    amount DECIMAL(10, 2), -- Calculated amount
    unit_price DECIMAL(10, 2), -- Price per unit at time of reading
    capture TEXT, -- Description of the reading
    image_url TEXT, -- Photo of meter reading
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    reader_id INTEGER, -- User who took the reading
    verification_status VARCHAR(50), -- verified, disputed, adjusted
    verified_by INTEGER, -- User who verified
    notes TEXT,
    reading_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (meter_id) REFERENCES meters(id),
    FOREIGN KEY (reader_id) REFERENCES users(id),
    FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- Create tariffs table
CREATE TABLE tariffs (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    customer_type VARCHAR(100), -- Loại khách hàng (hộ gia đình, doanh nghiệp)
    consumption_range_min DECIMAL(10, 2), -- Mức tiêu thụ tối thiểu
    consumption_range_max DECIMAL(10, 2), -- Mức tiêu thụ tối đa
    unit_price DECIMAL(10, 2), -- Đơn giá
    environmental_fee_percentage DECIMAL(5, 2), -- % phí môi trường
    vat_percentage DECIMAL(5, 2), -- % VAT
    effective_from DATE,
    effective_to DATE,
    status VARCHAR(50),
    zone_id INTEGER, -- If different zones have different tariffs
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (zone_id) REFERENCES distribution_zones(id)
);

-- Create bills table with expanded fields
CREATE TABLE bills (
    id SERIAL PRIMARY KEY,
    bill_number VARCHAR(50) UNIQUE,
    customer_id INTEGER NOT NULL,
    meter_id INTEGER NOT NULL,
    reading_id INTEGER, -- Link to the read_histories
    tariff_id INTEGER, -- Link to the applied tariff
    water_volume DECIMAL(10, 2) NOT NULL, -- m3 of water consumed
    unit_price DECIMAL(10, 2), -- Price per unit
    basic_charge DECIMAL(10, 2), -- Basic charge before taxes and fees
    environmental_fee DECIMAL(10, 2), -- Environmental protection fee
    tax_amount DECIMAL(10, 2), -- VAT
    discount_amount DECIMAL(10, 2) DEFAULT 0, -- Any discount
    total_amount DECIMAL(10, 2) NOT NULL, -- Total bill amount
    paid_amount DECIMAL(10, 2) DEFAULT 0, -- Số tiền đã thanh toán
    remaining_amount DECIMAL(10, 2), -- Số tiền còn lại cần thanh toán
    billing_period_start DATE,
    billing_period_end DATE,
    issue_date DATE,
    due_date DATE,
    bill_status VARCHAR(50), -- draft, issued, paid, overdue, cancelled
    payment_status VARCHAR(50), -- unpaid, partially_paid, paid
    order_value_amount DECIMAL(10, 2) NOT NULL,
    created_by INTEGER,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (meter_id) REFERENCES meters(id),
    FOREIGN KEY (reading_id) REFERENCES read_histories(id),
    FOREIGN KEY (tariff_id) REFERENCES tariffs(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create payment_histories table with expanded fields
CREATE TABLE payment_histories (
    id SERIAL PRIMARY KEY,
    payment_number VARCHAR(50) UNIQUE,
    customer_id INTEGER NOT NULL,
    amount DECIMAL(10, 2) NOT NULL, -- Tổng số tiền thanh toán
    previous_debt DECIMAL(10, 2), -- Dư nợ trước khi thanh toán
    remaining_debt DECIMAL(10, 2), -- Dư nợ sau khi thanh toán
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    payment_provider VARCHAR(100),
    payment_status VARCHAR(50),
    receipt_number VARCHAR(50),
    received_by INTEGER, -- User who received payment
    details TEXT,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (received_by) REFERENCES users(id)
);

-- Create payment_bill_allocations table to link payments to multiple bills
CREATE TABLE payment_bill_allocations (
    id SERIAL PRIMARY KEY,
    payment_id INTEGER NOT NULL,
    bill_id INTEGER NOT NULL,
    allocated_amount DECIMAL(10, 2) NOT NULL, -- Số tiền phân bổ cho hóa đơn này
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment_histories(id),
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    UNIQUE (payment_id, bill_id)
);

-- Create customer_debt_history to track debt changes
CREATE TABLE customer_debt_history (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    previous_debt DECIMAL(10, 2),
    new_debt DECIMAL(10, 2),
    change_amount DECIMAL(10, 2),
    change_type VARCHAR(50), -- bill_issued, payment, adjustment
    reference_id INTEGER, -- ID of bill or payment
    reference_type VARCHAR(50), -- bill, payment
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Create incidents table
CREATE TABLE incidents (
    id SERIAL PRIMARY KEY,
    incident_number VARCHAR(50) UNIQUE,
    incident_type VARCHAR(100), -- Rò rỉ, vỡ ống, ô nhiễm, etc.
    report_date TIMESTAMP,
    reported_by INTEGER, -- Liên kết với users
    customer_id INTEGER, -- If reported by a customer
    location_id INTEGER, -- Địa điểm xảy ra
    equipment_id INTEGER NULL, -- Thiết bị gặp sự cố (nếu có)
    meter_id INTEGER NULL, -- If meter related
    zone_id INTEGER, -- Affected zone
    severity VARCHAR(50), -- Mức độ nghiêm trọng
    priority VARCHAR(50),
    status VARCHAR(50), -- Trạng thái xử lý
    description TEXT,
    resolution TEXT,
    resolved_date TIMESTAMP,
    resolved_by INTEGER, -- Liên kết với users
    cost DECIMAL(10, 2), -- Chi phí xử lý
    affected_customers INTEGER, -- Số khách hàng bị ảnh hưởng
    estimated_water_loss DECIMAL(10, 2), -- Estimated water loss in m3
    downtime_hours DECIMAL(8, 2), -- Service downtime in hours
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES users(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (location_id) REFERENCES addresses(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (meter_id) REFERENCES meters(id),
    FOREIGN KEY (zone_id) REFERENCES distribution_zones(id),
    FOREIGN KEY (resolved_by) REFERENCES users(id)
);

-- Create reports table
CREATE TABLE reports (
    id SERIAL PRIMARY KEY,
    report_type VARCHAR(100), -- Loại báo cáo
    report_number VARCHAR(50) UNIQUE,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    report_period_start DATE,
    report_period_end DATE,
    generated_by INTEGER, -- Liên kết với users
    approved_by INTEGER, -- User who approved the report
    plant_id INTEGER NULL, -- Báo cáo cho nhà máy nào (nếu có)
    zone_id INTEGER NULL, -- Báo cáo cho khu vực nào (nếu có)
    file_url TEXT, -- URL đến file báo cáo
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (generated_by) REFERENCES users(id),
    FOREIGN KEY (approved_by) REFERENCES users(id),
    FOREIGN KEY (plant_id) REFERENCES water_plants(id),
    FOREIGN KEY (zone_id) REFERENCES distribution_zones(id)
);

-- Create notifications table
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    customer_id INTEGER,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    notification_type VARCHAR(50),
    related_entity_type VARCHAR(50), -- bill, task, incident, etc.
    related_entity_id INTEGER,
    is_read BOOLEAN DEFAULT false,
    sent_via VARCHAR(50), -- email, sms, app, etc.
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Create sequences for bill and payment numbers
CREATE SEQUENCE IF NOT EXISTS bill_number_seq START 1;
CREATE SEQUENCE IF NOT EXISTS payment_number_seq START 1;

-- Function to create new bill
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

-- Function to process payment
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

-- Function to allocate payments to bills
CREATE OR REPLACE FUNCTION allocate_payment_to_bills(
    p_payment_id INTEGER,
    p_allocation_data JSONB
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

-- Function to check customer debt limit
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

-- Function to get customer debt report
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

-- Function to calculate minimum payment
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

-- Add indexes for performance optimization
CREATE INDEX idx_role_permission_role_id ON role_permission(role_id);
CREATE INDEX idx_role_permission_permission_id ON role_permission(permission_id);
CREATE INDEX idx_user_role_user_id ON user_role(user_id);
CREATE INDEX idx_user_role_role_id ON user_role(role_id);
CREATE INDEX idx_users_information_id ON users(information_id);
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_customers_information_id ON customers(information_id);
CREATE INDEX idx_meters_customer_id ON meters(customer_id);
CREATE INDEX idx_meters_address_id ON meters(address_id);
CREATE INDEX idx_meters_zone_id ON meters(zone_id);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_tasks_meter_id ON tasks(meter_id);
CREATE INDEX idx_tasks_customer_id ON tasks(customer_id);
CREATE INDEX idx_tasks_equipment_id ON tasks(equipment_id);
CREATE INDEX idx_read_histories_task_id ON read_histories(task_id);
CREATE INDEX idx_read_histories_meter_id ON read_histories(meter_id);
CREATE INDEX idx_read_histories_customer_id ON read_histories(customer_id);
CREATE INDEX idx_bills_customer_id ON bills(customer_id);
CREATE INDEX idx_bills_meter_id ON bills(meter_id);
CREATE INDEX idx_bills_reading_id ON bills(reading_id);
CREATE INDEX idx_bills_tariff_id ON bills(tariff_id);
CREATE INDEX idx_payment_histories_customer_id ON payment_histories(customer_id);
CREATE INDEX idx_payment_histories_bill_id ON payment_histories(bill_id);
CREATE INDEX idx_informations_address_id ON informations(address_id);
CREATE INDEX idx_equipment_plant_id ON equipment(plant_id);
CREATE INDEX idx_maintenance_records_equipment_id ON maintenance_records(equipment_id);
CREATE INDEX idx_maintenance_records_meter_id ON maintenance_records(meter_id);
CREATE INDEX idx_incidents_zone_id ON incidents(zone_id);
CREATE INDEX idx_incidents_equipment_id ON incidents(equipment_id);
CREATE INDEX idx_incidents_meter_id ON incidents(meter_id);
CREATE INDEX idx_distribution_zones_plant_id ON distribution_zones(plant_id);
CREATE INDEX idx_reports_plant_id ON reports(plant_id);
CREATE INDEX idx_reports_zone_id ON reports(zone_id);
CREATE INDEX idx_bills_payment_status ON bills(payment_status);
CREATE INDEX idx_bills_remaining_amount ON bills(remaining_amount);
CREATE INDEX idx_customers_current_debt ON customers(current_debt);
CREATE INDEX idx_payment_bill_allocations_payment_id ON payment_bill_allocations(payment_id);
CREATE INDEX idx_payment_bill_allocations_bill_id ON payment_bill_allocations(bill_id);
CREATE INDEX idx_customer_debt_history_customer_id ON customer_debt_history(customer_id); 