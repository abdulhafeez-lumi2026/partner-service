CREATE TABLE partner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partner_id VARCHAR(36) NOT NULL UNIQUE,
    partner_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    api_key_hash VARCHAR(255),
    client_id VARCHAR(255),
    client_secret_hash VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'SANDBOX',
    rate_limit INT NOT NULL DEFAULT 60,
    commission_percentage DECIMAL(5,2),
    quote_mode VARCHAR(10) NOT NULL DEFAULT 'BOTH',
    booking_mode VARCHAR(20) NOT NULL DEFAULT 'PAY_LATER',
    allowed_branches TEXT,
    allowed_vehicle_groups TEXT,
    webhook_url VARCHAR(500),
    ip_whitelist TEXT,
    contract_valid_until DATE,
    version INT NOT NULL DEFAULT 0,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_partner_code ON partner(partner_code);
CREATE INDEX idx_partner_status ON partner(status);
CREATE INDEX idx_partner_client_id ON partner(client_id);
