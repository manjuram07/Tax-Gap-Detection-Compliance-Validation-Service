package com.bank.service;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RefundValidationRuleValidator implements TaxRuleValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(TaxRule taxRule) {
        return "REFUND_VALIDATION".equalsIgnoreCase(taxRule.getRuleType());
    }

    @Override
    public RuleViolation validate(FinancialTransaction transaction, TaxRule taxRule) {
        if (transaction == null || transaction.getAmount() == null || transaction.getOriginalSaleAmount() == null) return RuleViolation.ok();
        try {
            JsonNode node = taxRule.getConfig() == null ? mapper.createObjectNode() : mapper.readTree(taxRule.getConfig());
            String severity = node.has("severity") ? node.get("severity").asText() : "MEDIUM";

            BigDecimal refund = transaction.getAmount();
            BigDecimal original = transaction.getOriginalSaleAmount();

            if (refund.compareTo(original) > 0) {
                return RuleViolation.violated("Refund amount " + refund + " exceeds original sale amount " + original, severity);
            }
        } catch (Exception e) {
            // ignore parse errors
        }
        return RuleViolation.ok();
    }
}
