-- Database schema creation based on the provided ER diagram
-- Creating database tables with primary keys, foreign keys and relationships
-- BACKUP FILE

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
    FOREIGN KEY (information_id) REFERENCES informations(id)
);

-- Create meters table
CREATE TABLE meters (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    serial_number VARCHAR(100) NOT NULL,
    status VARCHAR(20),
    address_id INTEGER,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    installed_date DATE,
    last_maintain_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Create tasks table
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    "desc" TEXT,
    status VARCHAR(50),
    assigned_to INTEGER,
    created_by INTEGER,
    due_time TIMESTAMP,
    data_detail JSONB,
    FOREIGN KEY (assigned_to) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create read_histories table
CREATE TABLE read_histories (
    id SERIAL PRIMARY KEY,
    order_id INTEGER,
    task_id INTEGER,
    amount DECIMAL(10, 2),
    capture TEXT,
    previous_value DECIMAL(10, 2),
    image_url TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    notes TEXT,
    reading_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- Create bills table
CREATE TABLE bills (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    meter_id INTEGER NOT NULL,
    order_value_amount DECIMAL(10, 2) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (meter_id) REFERENCES meters(id)
);

-- Create payment_histories table
CREATE TABLE payment_histories (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    remaining_debts DECIMAL(10, 2),
    payment_method VARCHAR(50),
    details TEXT,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

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
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_read_histories_task_id ON read_histories(task_id);
CREATE INDEX idx_bills_customer_id ON bills(customer_id);
CREATE INDEX idx_bills_meter_id ON bills(meter_id);
CREATE INDEX idx_payment_histories_customer_id ON payment_histories(customer_id);
CREATE INDEX idx_informations_address_id ON informations(address_id); 