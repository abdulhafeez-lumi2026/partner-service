CREATE TABLE partner_api_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partner_id VARCHAR(36) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    response_status INT NOT NULL,
    response_time_ms BIGINT,
    request_date DATE NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_api_usage_partner FOREIGN KEY (partner_id) REFERENCES partner(partner_id)
);

CREATE INDEX idx_api_usage_partner_date ON partner_api_usage(partner_id, request_date);
