package com.bank.repository;

import com.bank.domain.AuditLog;
import com.bank.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByTransactionId(UUID transactionId);

    List<AuditLog> findByEventType(EventType eventType);

    List<AuditLog> findByEventTypeAndTransactionId(EventType eventType, UUID transactionId);

    List<AuditLog> findByEventTimestampBetween(OffsetDateTime start, OffsetDateTime end);

    List<AuditLog> findByEventTypeAndEventTimestampBetween(EventType eventType, OffsetDateTime start, OffsetDateTime end);

    List<AuditLog> findByCorrelationId(String correlationId);

    List<AuditLog> findByServiceName(String serviceName);
}
