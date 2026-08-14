package com.bank.service;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HighValueTransactionRuleValidator implements TaxRuleValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(TaxRule taxRule) {
        return "HIGH_VALUE".equalsIgnoreCase(taxRule.getRuleType());
    }

    @Override
    public RuleViolation validate(FinancialTransaction transaction, TaxRule taxRule) {
        if (transaction == null || transaction.getAmount() == null) return RuleViolation.ok();
        try {
            String cfg = taxRule.getConfig();
            JsonNode node = cfg == null ? mapper.createObjectNode() : mapper.readTree(cfg);
            BigDecimal threshold = node.has("threshold") ? new BigDecimal(node.get("threshold").asText()) : BigDecimal.ZERO;
            String severity = node.has("severity") ? node.get("severity").asText() : "HIGH";

            if (transaction.getAmount().compareTo(threshold) > 0) {
                return RuleViolation.violated("Transaction amount " + transaction.getAmount() + " exceeds threshold " + threshold, severity);
            }
        } catch (Exception e) {
            // on parse errors, treat as not violated
        }
        return RuleViolation.ok();
    }
}
