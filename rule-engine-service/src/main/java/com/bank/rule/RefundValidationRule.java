package com.bank.rule;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;
import com.bank.enums.Severity;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Component
public class RefundValidationRule implements TaxRuleValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(TaxRule taxRule) {
        return taxRule != null && "REFUND_VALIDATION".equalsIgnoreCase(taxRule.getRuleType());
    }

    @Override
    public RuleViolation validate(FinancialTransaction transaction, TaxRule taxRule) {
        if (transaction == null || transaction.getAmount() == null) return RuleViolation.pass();

        try {
            String cfg = taxRule.getConfig();
            JsonNode node = cfg == null ? mapper.createObjectNode() : mapper.readTree(cfg);
            String sev = node.has("severity") ? node.get("severity").asText() : taxRule.getSeverity();
            Severity severity;
            try { severity = sev == null ? Severity.MEDIUM : Severity.valueOf(sev.toUpperCase()); } catch (Exception e) { severity = Severity.MEDIUM; }

            BigDecimal refund = transaction.getAmount();
            BigDecimal original = transaction.getOriginalSaleAmount();

            if (original == null) {
                // if original not provided, can't validate here
                return RuleViolation.pass();
            }

            if (refund.compareTo(original) > 0) {
                return RuleViolation.fail(severity, "Refund amount " + refund + " exceeds original sale amount " + original);
            }
        } catch (Exception e) {
            // ignore parse
        }
        return RuleViolation.pass();
    }
}
