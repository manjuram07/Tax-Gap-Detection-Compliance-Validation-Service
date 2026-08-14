package com.bank.service;

import com.bank.domain.AuditLog;
import com.bank.enums.EventType;
import com.bank.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditLog logIngestion(UUID transactionId, Map<String, Object> ingestionDetails) {
        return logEvent(
            EventType.INGESTION,
            transactionId,
            ingestionDetails,
            null
        );
    }

    public AuditLog logRuleExecution(UUID transactionId, Map<String, Object> ruleDetails) {
        return logEvent(
            EventType.RULE_EXECUTION,
            transactionId,
            ruleDetails,
            null
        );
    }

    public AuditLog logTaxComputation(UUID transactionId, Map<String, Object> computationDetails) {
        return logEvent(
            EventType.TAX_COMPUTATION,
            transactionId,
            computationDetails,
            null
        );
    }

    public AuditLog logEventWithDetails(
        EventType eventType,
        UUID transactionId,
        Map<String, Object> details,
        String correlationId
    ) {
        return logEvent(eventType, transactionId, details, correlationId);
    }

    public AuditLog logEventWithOldNewValues(
        EventType eventType,
        UUID transactionId,
        Object oldValue,
        Object newValue,
        String changeDescription,
        String correlationId
    ) {
        Map<String, Object> details = Map.of(
            "changeDescription", changeDescription,
            "oldValue", oldValue != null ? oldValue : "null",
            "newValue", newValue != null ? newValue : "null"
        );
        return logEvent(eventType, transactionId, details, correlationId);
    }

    public Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        return objectMapper.convertValue(jsonNode, Map.class);
    }

    private AuditLog logEvent(
        EventType eventType,
        UUID transactionId,
        Map<String, Object> details,
        String correlationId
    ) {
        try {
            JsonNode detailJson = objectMapper.valueToTree(details);

            AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .transactionId(transactionId)
                .detailJson(detailJson)
                .eventTimestamp(OffsetDateTime.now())
                .correlationId(correlationId)
                .serviceName("AuditService")
                .build();

            AuditLog savedLog = auditLogRepository.save(auditLog);

            log.info("Audit log created: eventType={}, transactionId={}, logId={}",
                eventType, transactionId, savedLog.getId());

            return savedLog;
        } catch (Exception e) {
            log.error("Error logging audit event: eventType={}, transactionId={}, error={}",
                eventType, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to create audit log", e);
        }
    }

    public List<AuditLog> getAuditLogsByTransactionId(UUID transactionId) {
        return auditLogRepository.findByTransactionId(transactionId);
    }

    public List<AuditLog> getAuditLogsByEventType(EventType eventType) {
        return auditLogRepository.findByEventType(eventType);
    }

    public List<AuditLog> getAuditLogsByEventTypeAndTransactionId(EventType eventType, UUID transactionId) {
        return auditLogRepository.findByEventTypeAndTransactionId(eventType, transactionId);
    }

    public List<AuditLog> getAuditLogsByDateRange(OffsetDateTime start, OffsetDateTime end) {
        return auditLogRepository.findByEventTimestampBetween(start, end);
    }

    public List<AuditLog> getAuditLogsByEventTypeAndDateRange(EventType eventType, OffsetDateTime start, OffsetDateTime end) {
        return auditLogRepository.findByEventTypeAndEventTimestampBetween(eventType, start, end);
    }

    public List<AuditLog> getAuditLogsByCorrelationId(String correlationId) {
        return auditLogRepository.findByCorrelationId(correlationId);
    }

    public List<AuditLog> getAuditLogsByServiceName(String serviceName) {
        return auditLogRepository.findByServiceName(serviceName);
    }
}
