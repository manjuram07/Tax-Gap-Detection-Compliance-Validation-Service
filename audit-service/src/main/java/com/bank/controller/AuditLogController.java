package com.bank.controller;

import com.bank.domain.AuditLog;
import com.bank.dto.AuditLogDTO;
import com.bank.dto.AuditLogRequestDTO;
import com.bank.enums.EventType;
import com.bank.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Slf4j
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping("/log")
    public ResponseEntity<AuditLogDTO> logAuditEvent(@Valid @RequestBody AuditLogRequestDTO request) {
        log.info("Logging audit event: eventType={}, transactionId={}", request.getEventType(), request.getTransactionId());
        
        AuditLog auditLog = auditLogService.logEventWithDetails(
            request.getEventType(),
            request.getTransactionId(),
            auditLogService.convertJsonNodeToMap(request.getDetailJson()),
            request.getCorrelationId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(auditLog));
    }

    @PostMapping("/log-ingestion")
    public ResponseEntity<AuditLogDTO> logIngestion(
        @RequestParam UUID transactionId,
        @RequestBody Map<String, Object> ingestionDetails
    ) {
        log.info("Logging transaction ingestion: transactionId={}", transactionId);
        AuditLog auditLog = auditLogService.logIngestion(transactionId, ingestionDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(auditLog));
    }

    @PostMapping("/log-rule-execution")
    public ResponseEntity<AuditLogDTO> logRuleExecution(
        @RequestParam UUID transactionId,
        @RequestBody Map<String, Object> ruleDetails
    ) {
        log.info("Logging rule execution: transactionId={}", transactionId);
        AuditLog auditLog = auditLogService.logRuleExecution(transactionId, ruleDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(auditLog));
    }

    @PostMapping("/log-tax-computation")
    public ResponseEntity<AuditLogDTO> logTaxComputation(
        @RequestParam UUID transactionId,
        @RequestBody Map<String, Object> computationDetails
    ) {
        log.info("Logging tax computation: transactionId={}", transactionId);
        AuditLog auditLog = auditLogService.logTaxComputation(transactionId, computationDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(auditLog));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByTransaction(
        @PathVariable UUID transactionId
    ) {
        log.info("Fetching audit logs for transaction: {}", transactionId);
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByTransactionId(transactionId);
        List<AuditLogDTO> dtos = auditLogs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/event-type/{eventType}")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByEventType(
        @PathVariable EventType eventType
    ) {
        log.info("Fetching audit logs for event type: {}", eventType);
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByEventType(eventType);
        List<AuditLogDTO> dtos = auditLogs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/transaction/{transactionId}/event-type/{eventType}")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByEventTypeAndTransaction(
        @PathVariable EventType eventType,
        @PathVariable UUID transactionId
    ) {
        log.info("Fetching audit logs for event type: {} and transaction: {}", eventType, transactionId);
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByEventTypeAndTransactionId(eventType, transactionId);
        List<AuditLogDTO> dtos = auditLogs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end
    ) {
        log.info("Fetching audit logs between: {} and {}", start, end);
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByDateRange(start, end);
        List<AuditLogDTO> dtos = auditLogs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/correlation-id/{correlationId}")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByCorrelationId(
        @PathVariable String correlationId
    ) {
        log.info("Fetching audit logs for correlation id: {}", correlationId);
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByCorrelationId(correlationId);
        List<AuditLogDTO> dtos = auditLogs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private AuditLogDTO mapToDTO(AuditLog auditLog) {
        return AuditLogDTO.builder()
            .id(auditLog.getId())
            .eventType(auditLog.getEventType())
            .transactionId(auditLog.getTransactionId())
            .eventTimestamp(auditLog.getEventTimestamp())
            .detailJson(auditLog.getDetailJson())
            .correlationId(auditLog.getCorrelationId())
            .serviceName(auditLog.getServiceName())
            .createdAt(auditLog.getCreatedAt())
            .build();
    }
}
