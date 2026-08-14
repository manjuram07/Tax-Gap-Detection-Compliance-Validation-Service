package com.bank.service;

import com.bank.enums.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionIngestionService {

    private final AuditLogService auditLogService;

    public void ingestTransaction(UUID transactionId, Map<String, Object> transactionData) {
        log.info("Starting transaction ingestion for transactionId: {}", transactionId);

        try {
            Map<String, Object> ingestionDetails = new HashMap<>();
            ingestionDetails.put("transactionData", transactionData);
            ingestionDetails.put("status", "INGESTED");
            ingestionDetails.put("recordCount", ((Map) transactionData).size());

            auditLogService.logIngestion(transactionId, ingestionDetails);

            log.info("Transaction ingestion completed for transactionId: {}", transactionId);
        } catch (Exception e) {
            log.error("Error during transaction ingestion for transactionId: {}", transactionId, e);
            throw new RuntimeException("Transaction ingestion failed", e);
        }
    }

    public void ingestTransactionBatch(UUID correlationId, Map<String, Object> batchData) {
        log.info("Starting batch transaction ingestion with correlationId: {}", correlationId);

        try {
            Map<String, Object> ingestionDetails = new HashMap<>();
            ingestionDetails.put("batchData", batchData);
            ingestionDetails.put("status", "BATCH_INGESTED");

            UUID transactionId = UUID.randomUUID();
            auditLogService.logEventWithDetails(
                EventType.INGESTION,
                transactionId,
                ingestionDetails,
                correlationId.toString()
            );

            log.info("Batch transaction ingestion completed with correlationId: {}", correlationId);
        } catch (Exception e) {
            log.error("Error during batch transaction ingestion with correlationId: {}", correlationId, e);
            throw new RuntimeException("Batch ingestion failed", e);
        }
    }
}
