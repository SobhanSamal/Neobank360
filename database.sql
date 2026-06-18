CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','CUSTOMER') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15,2) DEFAULT 0.00,
    account_type ENUM('SAVING','CURRENT') NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    gender VARCHAR(20),
    dob VARCHAR(20) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    alternate_mobile VARCHAR(10),
    email VARCHAR(255) NOT NULL,
    aadhaar VARCHAR(12) NOT NULL,
    pan VARCHAR(10) NOT NULL,
    address_line1 VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(50),
    district VARCHAR(100),
    pincode VARCHAR(6),
    nominee_name VARCHAR(255),
    nominee_relation VARCHAR(50),
    nominee_mobile VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    type ENUM('DEBIT','CREDIT') NOT NULL,
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    description VARCHAR(500),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    balance_after DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Insert initial admin user (email: admin@neobank.com, password: admin123)
INSERT INTO users (email, password_hash, full_name, role, is_active, created_at)
VALUES ('admin@neobank.com', '$2a$10$lTCFsgf9EzCSGb2AEubNVuainFcFnTdvVpk7wPTKPxvmNT.JQn2I.', 'Admin User', 'ADMIN', TRUE, NOW())
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    full_name = VALUES(full_name),
    role = VALUES(role),
    is_active = VALUES(is_active);

-- Insert sample customer user (email: customer@neobank.com, password: customer123)
INSERT INTO users (email, password_hash, full_name, role, is_active, created_at)
VALUES ('customer@neobank.com', '$2a$10$zY7CB5F2LMSGJi4SHsCmFuN6U/V79ddD8GoseFqWnWNtI0M6gWv2O', 'John Doe', 'CUSTOMER', TRUE, NOW())
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    full_name = VALUES(full_name),
    role = VALUES(role),
    is_active = VALUES(is_active);