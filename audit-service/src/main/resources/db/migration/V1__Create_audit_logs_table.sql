CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(36),
    event_timestamp DATETIME NOT NULL,
    detail_json JSON,
    tax_details JSON,
    correlation_id VARCHAR(100),
    service_name VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_event_type (event_type),
    INDEX idx_event_timestamp (event_timestamp),
    INDEX idx_correlation_id (correlation_id),
    INDEX idx_service_name (service_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
