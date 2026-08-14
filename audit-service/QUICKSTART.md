# Quick Start Guide - Audit Logging Service

## Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.8+

## Setup Steps

### 1. Database Setup
```bash
# Create database
mysql -u root -p
CREATE DATABASE IF NOT EXISTS audit_db;
EXIT;
```

### 2. Build Project
```bash
cd audit-service
mvn clean package -DskipTests
```

### 3. Run Application
```bash
java -jar target/audit-service-0.0.1-SNAPSHOT.jar
```

Or using Maven:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:6003`

## Database Migrations
Flyway will automatically run migrations on startup, creating the `audit_logs` table.

## Testing

### Run All Tests
```bash
mvn test
```

### Expected Result
```
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
```

## API Examples

### 1. Log Transaction Ingestion
```bash
curl -X POST http://localhost:6003/api/audit-logs/log-ingestion?transactionId=550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "INGESTED",
    "recordCount": 10,
    "timestamp": "'$(date -u +%Y-%m-%dT%H:%M:%SZ)'"
  }'
```

### 2. Log Rule Execution
```bash
curl -X POST http://localhost:6003/api/audit-logs/log-rule-execution?transactionId=550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "ruleName": "SuspiciousActivityRule",
    "ruleResult": true,
    "ruleParameters": {
      "threshold": 10000,
      "timeWindow": "24h"
    }
  }'
```

### 3. Log Tax Computation
```bash
curl -X POST http://localhost:6003/api/audit-logs/log-tax-computation?transactionId=550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000,
    "taxType": "GST",
    "calculatedTax": 180
  }'
```

### 4. Retrieve Logs by Transaction
```bash
curl http://localhost:6003/api/audit-logs/transaction/550e8400-e29b-41d4-a716-446655440000
```

### 5. Retrieve Logs by Event Type
```bash
curl http://localhost:6003/api/audit-logs/event-type/INGESTION
```

### 6. Retrieve Logs by Date Range
```bash
curl "http://localhost:6003/api/audit-logs/date-range?start=2024-01-01T00:00:00Z&end=2024-01-31T23:59:59Z"
```

### 7. Retrieve Logs by Correlation ID
```bash
curl http://localhost:6003/api/audit-logs/correlation-id/correlation-123
```

## Integration with Services

### Using AuditLogService in Another Service
```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final AuditLogService auditLogService;

    public void myBusinessLogic(UUID transactionId) {
        // Your business logic here
        
        // Log the event
        auditLogService.logIngestion(transactionId, Map.of(
            "action", "PROCESSED",
            "status", "SUCCESS"
        ));
    }
}
```

## Troubleshooting

### Issue: Database Connection Failed
**Solution**: Ensure MySQL is running and database credentials in `application.properties` are correct.

### Issue: Migration Failed
**Solution**: Check that `src/main/resources/db/migration` folder exists and contains migration files.

### Issue: Tests Failing
**Solution**: 
1. Ensure Java 21 is installed: `java -version`
2. Clear Maven cache: `mvn clean`
3. Rebuild: `mvn test`

## Application Properties Configuration

Key properties in `application.properties`:
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/audit_db
spring.datasource.username=root
spring.datasource.password=root

# Server
server.port=6003

# Logging
logging.level.com.bank=DEBUG
logging.file.name=logs/audit-service.log
```

## View Logs
```bash
tail -f logs/audit-service.log
```

## Performance Tips

1. **Indexing**: All frequently queried fields are indexed
2. **Connection Pooling**: HikariCP is configured with optimal pool size
3. **JSON Storage**: Use `detailJson` field for flexible data without schema changes
4. **Date Range Queries**: Always include date filters for optimal performance

## Additional Resources

- Detailed Documentation: See `AUDIT_LOGGING_README.md`
- Implementation Details: See `IMPLEMENTATION_SUMMARY.md`
- API Swagger: Available at `http://localhost:6003/swagger-ui.html` (requires springdoc-openapi-ui)

## Support

For issues or questions:
1. Check the logs in `logs/audit-service.log`
2. Review the documentation files
3. Run tests to verify setup: `mvn test`
