package com.bank.service;

import com.bank.domain.AuditLog;
import com.bank.enums.EventType;
import com.bank.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditLogService auditLogService;

    private UUID testTransactionId;
    private Map<String, Object> testDetails;
    private AuditLog testAuditLog;

    @BeforeEach
    void setUp() {
        testTransactionId = UUID.randomUUID();
        testDetails = new HashMap<>();
        testDetails.put("status", "INGESTED");
        testDetails.put("recordCount", 10);

        testAuditLog = AuditLog.builder()
            .id(UUID.randomUUID())
            .eventType(EventType.INGESTION)
            .transactionId(testTransactionId)
            .serviceName("AuditService")
            .build();
    }

    @Test
    void testLogIngestion_Success() {
        // Setup
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // Execute
        AuditLog result = auditLogService.logIngestion(testTransactionId, testDetails);

        // Assert
        assertNotNull(result);
        assertEquals(EventType.INGESTION, result.getEventType());
        assertEquals(testTransactionId, result.getTransactionId());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogRuleExecution_Success() {
        // Setup
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // Execute
        AuditLog result = auditLogService.logRuleExecution(testTransactionId, testDetails);

        // Assert
        assertNotNull(result);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogTaxComputation_Success() {
        // Setup
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // Execute
        AuditLog result = auditLogService.logTaxComputation(testTransactionId, testDetails);

        // Assert
        assertNotNull(result);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogEventWithOldNewValues_Success() {
        // Setup
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // Execute
        AuditLog result = auditLogService.logEventWithOldNewValues(
            EventType.TAX_COMPUTATION,
            testTransactionId,
            100,
            150,
            "Tax amount adjusted",
            "corr-123"
        );

        // Assert
        assertNotNull(result);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
