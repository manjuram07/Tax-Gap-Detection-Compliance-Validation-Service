package com.bank.service;

import com.bank.domain.AuditLog;
import com.bank.enums.EventType;
import com.bank.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    private AuditLog logEvent(
        EventType eventType,
        UUID transactionId,
        Map<String, Object> details,
        String correlationId
    ) {
        try {
            JsonNode detailJson = mapToJsonNode(details);

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

    private JsonNode mapToJsonNode(Map<String, Object> map) {
        if (map == null) {
            return JsonNodeFactory.instance.nullNode();
        }
        
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                node.putNull(entry.getKey());
            } else if (value instanceof String) {
                node.put(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                node.put(entry.getKey(), (Integer) value);
            } else if (value instanceof Long) {
                node.put(entry.getKey(), (Long) value);
            } else if (value instanceof Double) {
                node.put(entry.getKey(), (Double) value);
            } else if (value instanceof Boolean) {
                node.put(entry.getKey(), (Boolean) value);
            } else {
                node.put(entry.getKey(), value.toString());
            }
        }
        return node;
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
