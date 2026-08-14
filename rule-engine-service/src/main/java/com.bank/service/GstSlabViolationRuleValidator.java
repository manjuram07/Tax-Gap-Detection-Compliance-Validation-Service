package com.bank.service;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GstSlabViolationRuleValidator implements TaxRuleValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(TaxRule taxRule) {
        return "GST_SLAB_VIOLATION".equalsIgnoreCase(taxRule.getRuleType());
    }

    @Override
    public RuleViolation validate(FinancialTransaction transaction, TaxRule taxRule) {
        if (transaction == null || transaction.getAmount() == null || transaction.getTaxRate() == null) return RuleViolation.ok();
        try {
            JsonNode node = taxRule.getConfig() == null ? mapper.createObjectNode() : mapper.readTree(taxRule.getConfig());

            BigDecimal slabThreshold = node.has("slabThreshold") ? new BigDecimal(node.get("slabThreshold").asText()) : null;
            BigDecimal requiredTaxRate = node.has("requiredTaxRate") ? new BigDecimal(node.get("requiredTaxRate").asText()) : null;
            String severity = node.has("severity") ? node.get("severity").asText() : "MEDIUM";

            if (slabThreshold != null && requiredTaxRate != null) {
                if (transaction.getAmount().compareTo(slabThreshold) > 0 && transaction.getTaxRate().compareTo(requiredTaxRate) < 0) {
                    return RuleViolation.violated("Amount " + transaction.getAmount() + " exceeds slab " + slabThreshold + " but taxRate " + transaction.getTaxRate() + " is below required " + requiredTaxRate, severity);
                }
            }
        } catch (Exception e) {
            // ignore parse errors
        }
        return RuleViolation.ok();
    }
}
