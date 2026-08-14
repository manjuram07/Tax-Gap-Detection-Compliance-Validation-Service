package com.bank.controller;

import com.bank.domain.AuditLog;
import com.bank.dto.AuditLogDTO;
import com.bank.dto.AuditLogRequestDTO;
import com.bank.enums.EventType;
import com.bank.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    private UUID testTransactionId;
    private AuditLog testAuditLog;

    @BeforeEach
    void setUp() {
        testTransactionId = UUID.randomUUID();

        testAuditLog = AuditLog.builder()
            .id(UUID.randomUUID())
            .eventType(EventType.INGESTION)
            .transactionId(testTransactionId)
            .eventTimestamp(OffsetDateTime.now())
            .serviceName("AuditService")
            .createdAt(OffsetDateTime.now())
            .build();
    }

    @Test
    void testLogIngestion_Success() {
        // Setup
        when(auditLogService.logIngestion(any(UUID.class), any()))
            .thenReturn(testAuditLog);

        // Execute
        ResponseEntity<AuditLogDTO> response = auditLogController.logIngestion(
            testTransactionId,
            new HashMap<>()
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EventType.INGESTION, response.getBody().getEventType());
    }

    @Test
    void testLogRuleExecution_Success() {
        // Setup
        testAuditLog.setEventType(EventType.RULE_EXECUTION);
        when(auditLogService.logRuleExecution(any(UUID.class), any()))
            .thenReturn(testAuditLog);

        // Execute
        ResponseEntity<AuditLogDTO> response = auditLogController.logRuleExecution(
            testTransactionId,
            new HashMap<>()
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(EventType.RULE_EXECUTION, response.getBody().getEventType());
    }

    @Test
    void testLogTaxComputation_Success() {
        // Setup
        testAuditLog.setEventType(EventType.TAX_COMPUTATION);
        when(auditLogService.logTaxComputation(any(UUID.class), any()))
            .thenReturn(testAuditLog);

        // Execute
        ResponseEntity<AuditLogDTO> response = auditLogController.logTaxComputation(
            testTransactionId,
            new HashMap<>()
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(EventType.TAX_COMPUTATION, response.getBody().getEventType());
    }

    @Test
    void testGetAuditLogsByTransaction_Success() {
        // Setup
        List<AuditLog> auditLogs = new ArrayList<>();
        auditLogs.add(testAuditLog);
        when(auditLogService.getAuditLogsByTransactionId(testTransactionId))
            .thenReturn(auditLogs);

        // Execute
        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getAuditLogsByTransaction(testTransactionId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAuditLogsByEventType_Success() {
        // Setup
        List<AuditLog> auditLogs = new ArrayList<>();
        auditLogs.add(testAuditLog);
        when(auditLogService.getAuditLogsByEventType(EventType.INGESTION))
            .thenReturn(auditLogs);

        // Execute
        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getAuditLogsByEventType(EventType.INGESTION);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAuditLogsByCorrelationId_Success() {
        // Setup
        List<AuditLog> auditLogs = new ArrayList<>();
        auditLogs.add(testAuditLog);
        when(auditLogService.getAuditLogsByCorrelationId("corr-123"))
            .thenReturn(auditLogs);

        // Execute
        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getAuditLogsByCorrelationId("corr-123");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}

