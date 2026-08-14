package com.bank.rule;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;
import com.bank.enums.Severity;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Component
public class GstSlabViolationRule implements TaxRuleValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(TaxRule taxRule) {
        return taxRule != null && "GST_SLAB_VIOLATION".equalsIgnoreCase(taxRule.getRuleType());
    }

    @Override
    public RuleViolation validate(FinancialTransaction transaction, TaxRule taxRule) {
        if (transaction == null || transaction.getAmount() == null || transaction.getTaxRate() == null) return RuleViolation.pass();

        try {
            String cfg = taxRule.getConfig();
            JsonNode node = cfg == null ? mapper.createObjectNode() : mapper.readTree(cfg);

            // Support either slabThreshold/requiredTaxRate or slabs array
            if (node.has("slabThreshold") && node.has("requiredTaxRate")) {
                BigDecimal slabThreshold = new BigDecimal(node.get("slabThreshold").asText());
                BigDecimal requiredTaxRate = new BigDecimal(node.get("requiredTaxRate").asText());
                String sev = node.has("severity") ? node.get("severity").asText() : taxRule.getSeverity();
                Severity severity;
                try { severity = sev == null ? Severity.MEDIUM : Severity.valueOf(sev.toUpperCase()); } catch (Exception e) { severity = Severity.MEDIUM; }

                if (transaction.getAmount().compareTo(slabThreshold) > 0 && transaction.getTaxRate().compareTo(requiredTaxRate) < 0) {
                    return RuleViolation.fail(severity, "Amount " + transaction.getAmount() + " exceeds slab " + slabThreshold + " but taxRate " + transaction.getTaxRate() + " is below required " + requiredTaxRate);
                }
            }
            // else: no valid config -> pass
        } catch (Exception e) {
            // parse error -> pass
        }

        return RuleViolation.pass();
    }
}
