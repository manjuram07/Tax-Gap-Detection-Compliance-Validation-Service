# Audit Logging Service

This microservice provides comprehensive audit logging functionality for the Tax Gap Detection and Compliance Validation System. It logs all critical business events including transaction ingestion, rule execution, and tax computation.

## Features

- **Transaction Ingestion Logging**: Tracks every transaction ingestion event with detailed metadata
- **Rule Execution Logging**: Records all rule executions including parameters, results, and outcomes
- **Tax Computation Logging**: Logs tax calculations with amounts, types, and results
- **Detailed Event Tracking**: Captures event type, transaction ID, timestamp, and detailed JSON data
- **RESTful API**: Exposes endpoints for logging events and retrieving audit records
- **Database Persistence**: Stores all audit logs in MySQL with proper indexing
- **Flexible Querying**: Query logs by transaction ID, event type, timestamp ranges, and correlation IDs

## Architecture

### Core Components

1. **AuditLog Entity** (`com.bank.domain.AuditLog`)
   - Main domain entity for storing audit log records
   - Fields: id, eventType, transactionId, eventTimestamp, detailJson, correlationId, serviceName, createdAt

2. **AuditLogRepository** (`com.bank.repository.AuditLogRepository`)
   - JPA repository for database operations
   - Provides query methods for various filtering scenarios

3. **AuditLogService** (`com.bank.service.AuditLogService`)
   - Core service for logging audit events
   - Methods:
     - `logIngestion()`: Log transaction ingestion
     - `logRuleExecution()`: Log rule execution events
     - `logTaxComputation()`: Log tax computation events
     - `logEventWithDetails()`: Generic event logging
     - `logEventWithOldNewValues()`: Log changes with old/new values

4. **AuditLogController** (`com.bank.controller.AuditLogController`)
   - REST API endpoints for logging and retrieving audit logs
   - Base path: `/api/audit-logs`

### Supporting Services

1. **TransactionIngestionService** (`com.bank.service.TransactionIngestionService`)
   - Demonstrates how to integrate audit logging for transaction ingestion
   - Methods:
     - `ingestTransaction()`: Ingest single transaction with logging
     - `ingestTransactionBatch()`: Ingest batch transactions with logging

2. **RuleExecutionService** (`com.bank.service.RuleExecutionService`)
   - Demonstrates audit logging for rule execution
   - Methods:
     - `executeRule()`: Execute single rule with logging
     - `executeRuleSequence()`: Execute multiple rules in sequence with logging

3. **TaxComputationService** (`com.bank.service.TaxComputationService`)
   - Demonstrates audit logging for tax computation
   - Methods:
     - `computeTax()`: Compute tax with logging
     - `computeMultipleTaxes()`: Compute multiple tax types with logging
     - `adjustTax()`: Log tax adjustments with old/new values

## Event Types

The system supports three main event types:

```java
public enum EventType {
    INGESTION,           // Transaction ingestion events
    RULE_EXECUTION,      // Rule execution events
    TAX_COMPUTATION      // Tax computation events
}
```

## API Endpoints

### Logging Endpoints

1. **Generic Log Event**
   ```
   POST /api/audit-logs/log
   Content-Type: application/json
   
   {
     "eventType": "INGESTION",
     "transactionId": "550e8400-e29b-41d4-a716-446655440000",
     "detailJson": {
       "status": "INGESTED",
       "recordCount": 10
     },
     "correlationId": "corr-123"
   }
   ```

2. **Log Ingestion Event**
   ```
   POST /api/audit-logs/log-ingestion?transactionId=550e8400-e29b-41d4-a716-446655440000
   Content-Type: application/json
   
   {
     "transactionData": { ... },
     "status": "INGESTED"
   }
   ```

3. **Log Rule Execution**
   ```
   POST /api/audit-logs/log-rule-execution?transactionId=550e8400-e29b-41d4-a716-446655440000
   Content-Type: application/json
   
   {
     "ruleName": "SuspiciousActivityRule",
     "ruleParameters": { ... },
     "ruleResult": true
   }
   ```

4. **Log Tax Computation**
   ```
   POST /api/audit-logs/log-tax-computation?transactionId=550e8400-e29b-41d4-a716-446655440000
   Content-Type: application/json
   
   {
     "amount": 1000,
     "taxType": "GST",
     "calculatedTax": 180
   }
   ```

### Query Endpoints

1. **Get Logs by Transaction ID**
   ```
   GET /api/audit-logs/transaction/{transactionId}
   ```

2. **Get Logs by Event Type**
   ```
   GET /api/audit-logs/event-type/{eventType}
   ```

3. **Get Logs by Event Type and Transaction ID**
   ```
   GET /api/audit-logs/transaction/{transactionId}/event-type/{eventType}
   ```

4. **Get Logs by Date Range**
   ```
   GET /api/audit-logs/date-range?start=2024-01-01T00:00:00Z&end=2024-01-31T23:59:59Z
   ```

5. **Get Logs by Correlation ID**
   ```
   GET /api/audit-logs/correlation-id/{correlationId}
   ```

## Usage Examples

### Using AuditLogService Directly

```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final AuditLogService auditLogService;

    public void processTransaction(UUID transactionId, Map<String, Object> data) {
        // Log ingestion
        auditLogService.logIngestion(transactionId, Map.of(
            "status", "INGESTED",
            "recordCount", data.size()
        ));

        // Process rules
        auditLogService.logRuleExecution(transactionId, Map.of(
            "ruleName", "MyRule",
            "ruleResult", true
        ));

        // Log tax computation
        auditLogService.logTaxComputation(transactionId, Map.of(
            "amount", 1000,
            "taxType", "GST",
            "calculatedTax", 180
        ));

        // Log changes
        auditLogService.logEventWithOldNewValues(
            EventType.TAX_COMPUTATION,
            transactionId,
            100,
            150,
            "Tax amount adjusted due to correction",
            "correlation-123"
        );
    }
}
```

### Using the REST API

```bash
# Log an ingestion event
curl -X POST http://localhost:6003/api/audit-logs/log-ingestion?transactionId=550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "INGESTED",
    "recordCount": 10
  }'

# Retrieve all logs for a transaction
curl http://localhost:6003/api/audit-logs/transaction/550e8400-e29b-41d4-a716-446655440000

# Retrieve logs by event type
curl http://localhost:6003/api/audit-logs/event-type/RULE_EXECUTION
```

## Database Schema

The audit logs are stored in the `audit_logs` table with the following structure:

| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(36) | UUID primary key |
| event_type | VARCHAR(50) | Type of event (INGESTION, RULE_EXECUTION, TAX_COMPUTATION) |
| transaction_id | VARCHAR(36) | UUID of the transaction |
| event_timestamp | DATETIME | When the event occurred |
| detail_json | JSON | Detailed event information |
| tax_details | JSON | Tax-related details |
| correlation_id | VARCHAR(100) | Correlation ID for tracing |
| service_name | VARCHAR(100) | Name of the service that created the log |
| created_at | DATETIME | When the log was created |

**Indexes:**
- idx_transaction_id
- idx_event_type
- idx_event_timestamp
- idx_correlation_id
- idx_service_name

## Configuration

### Application Properties

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/audit_db
spring.datasource.username=root
spring.datasource.password=root

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Flyway Configuration (Database Migrations)
spring.flyway.baselineOnMigrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.enabled=true

# Jackson Configuration
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.write-dates-as-timestamps=false
```

## Database Setup

### Create Database

```sql
CREATE DATABASE IF NOT EXISTS audit_db;
```

### Run Migrations

Flyway migrations are automatically run on application startup. The migration file `V1__Create_audit_logs_table.sql` creates the `audit_logs` table with proper schema and indexes.

## Error Handling

The system includes comprehensive error handling:

1. **AuditLoggingException**: Custom exception for audit logging errors
2. **GlobalExceptionHandler**: Centralized exception handling with consistent error responses
3. **Validation**: Request validation with detailed error messages

## Logging

The service uses SLF4J for logging. Configuration:

```properties
logging.level.root=INFO
logging.level.com.bank=DEBUG
logging.file.name=logs/audit-service.log
```

Log entries include:
- Audit event creation with log ID
- Errors during event logging
- Service lifecycle events

## Testing

Unit tests are included for:
- AuditLogService methods
- AuditLogController endpoints
- Error handling scenarios

Run tests:
```bash
mvn test
```

## Deployment

### Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.8+

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/audit-service-0.0.1-SNAPSHOT.jar
```

The service will start on `http://localhost:6003`

## Performance Considerations

1. **Indexing**: All commonly queried fields are indexed
2. **JSON Storage**: Detail information stored as JSON for flexibility
3. **Connection Pooling**: HikariCP with configured pool size
4. **Batch Operations**: Support for batch ingestion with correlation IDs
5. **Async Logging**: Can be extended to use asynchronous logging

## Security Considerations

1. **Input Validation**: All API inputs are validated
2. **Error Messages**: Generic error messages in responses (detailed logs server-side)
3. **SQL Injection Prevention**: JPA prevents SQL injection
4. **Data Sensitivity**: Sensitive data should be masked before logging

## Future Enhancements

1. **Async Logging**: Implement asynchronous audit logging for better performance
2. **Log Retention Policies**: Add archival and retention rules
3. **Encryption**: Encrypt sensitive fields in audit logs
4. **Real-time Alerts**: Stream audit events to real-time processing system
5. **Analytics**: Add pre-built dashboards for audit analytics
6. **Compliance Reporting**: Generate compliance reports from audit logs

## Support

For issues or questions, contact the development team or create an issue in the repository.
