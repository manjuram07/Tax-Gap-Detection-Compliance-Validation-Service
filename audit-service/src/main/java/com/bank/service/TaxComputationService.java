package com.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaxComputationService {

    private final AuditLogService auditLogService;

    public BigDecimal computeTax(UUID transactionId, BigDecimal amount, String taxType) {
        log.info("Computing tax for transactionId: {}, amount: {}, taxType: {}", transactionId, amount, taxType);

        try {
            BigDecimal taxAmount = calculateTaxAmount(amount, taxType);

            Map<String, Object> computationDetails = new HashMap<>();
            computationDetails.put("amount", amount);
            computationDetails.put("taxType", taxType);
            computationDetails.put("calculatedTax", taxAmount);
            computationDetails.put("computationStatus", "SUCCESS");

            auditLogService.logTaxComputation(transactionId, computationDetails);

            log.info("Tax computation completed for transactionId: {}, tax: {}", transactionId, taxAmount);
            return taxAmount;
        } catch (Exception e) {
            log.error("Error computing tax for transactionId: {}", transactionId, e);

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("amount", amount);
            errorDetails.put("taxType", taxType);
            errorDetails.put("computationStatus", "FAILED");
            errorDetails.put("errorMessage", e.getMessage());

            auditLogService.logTaxComputation(transactionId, errorDetails);
            throw new RuntimeException("Tax computation failed", e);
        }
    }

    public Map<String, BigDecimal> computeMultipleTaxes(UUID transactionId, BigDecimal amount, String[] taxTypes) {
        log.info("Computing multiple taxes for transactionId: {}, amount: {}, taxTypes count: {}", 
            transactionId, amount, taxTypes.length);

        try {
            Map<String, BigDecimal> taxResults = new HashMap<>();

            for (String taxType : taxTypes) {
                BigDecimal tax = calculateTaxAmount(amount, taxType);
                taxResults.put(taxType, tax);
            }

            BigDecimal totalTax = taxResults.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> computationDetails = new HashMap<>();
            computationDetails.put("baseAmount", amount);
            computationDetails.put("taxTypes", taxTypes);
            computationDetails.put("taxBreakdown", taxResults);
            computationDetails.put("totalTax", totalTax);
            computationDetails.put("computationStatus", "SUCCESS");

            auditLogService.logTaxComputation(transactionId, computationDetails);

            log.info("Multiple tax computation completed for transactionId: {}, totalTax: {}", transactionId, totalTax);
            return taxResults;
        } catch (Exception e) {
            log.error("Error computing multiple taxes for transactionId: {}", transactionId, e);

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("baseAmount", amount);
            errorDetails.put("taxTypes", taxTypes);
            errorDetails.put("computationStatus", "FAILED");
            errorDetails.put("errorMessage", e.getMessage());

            auditLogService.logTaxComputation(transactionId, errorDetails);
            throw new RuntimeException("Multiple tax computation failed", e);
        }
    }

    public void adjustTax(UUID transactionId, BigDecimal originalTax, BigDecimal adjustedTax, String reason) {
        log.info("Adjusting tax for transactionId: {}, reason: {}", transactionId, reason);

        try {
            auditLogService.logEventWithOldNewValues(
                com.bank.enums.EventType.TAX_COMPUTATION,
                transactionId,
                originalTax,
                adjustedTax,
                reason,
                null
            );

            log.info("Tax adjustment completed for transactionId: {}", transactionId);
        } catch (Exception e) {
            log.error("Error adjusting tax for transactionId: {}", transactionId, e);
            throw new RuntimeException("Tax adjustment failed", e);
        }
    }

    private BigDecimal calculateTaxAmount(BigDecimal amount, String taxType) {
        if ("GST".equalsIgnoreCase(taxType)) {
            return amount.multiply(new BigDecimal("0.18"));
        } else if ("VAT".equalsIgnoreCase(taxType)) {
            return amount.multiply(new BigDecimal("0.20"));
        } else if ("INCOME_TAX".equalsIgnoreCase(taxType)) {
            return amount.multiply(new BigDecimal("0.30"));
        } else {
            return BigDecimal.ZERO;
        }
    }
}
