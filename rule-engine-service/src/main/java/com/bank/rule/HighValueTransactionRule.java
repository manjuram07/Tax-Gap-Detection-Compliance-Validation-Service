package com.bank.rule;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;
import com.bank.enums.Severity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HighValueTransactionRule implements TaxRuleValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(TaxRule taxRule) {
        return taxRule != null && "HIGH_VALUE".equalsIgnoreCase(taxRule.getRuleType());
    }

    @Override
    public RuleViolation validate(FinancialTransaction transaction, TaxRule taxRule) {
        if (transaction == null || transaction.getAmount() == null) return RuleViolation.pass();

        try {
            String cfg = taxRule.getConfig();
            JsonNode node = cfg == null ? mapper.createObjectNode() : mapper.readTree(cfg);
            BigDecimal threshold = node.has("threshold") ? new BigDecimal(node.get("threshold").asText()) : BigDecimal.ZERO;
            String sev = node.has("severity") ? node.get("severity").asText() : taxRule.getSeverity();
            Severity severity;
            try { severity = sev == null ? Severity.HIGH : Severity.valueOf(sev.toUpperCase()); } catch (Exception e) { severity = Severity.HIGH; }

            if (transaction.getAmount().compareTo(threshold) > 0) {
                return RuleViolation.fail(severity, "Transaction amount " + transaction.getAmount() + " exceeds threshold " + threshold);
            }
        } catch (Exception e) {
            // parsing error - pass
        }
        return RuleViolation.pass();
    }
}
