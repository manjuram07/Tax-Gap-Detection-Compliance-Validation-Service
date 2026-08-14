package com.bank.service;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;
import com.bank.dto.TransactionRequest;
import com.bank.dto.Violation;
import com.bank.repository.TaxRuleRepository;
import com.bank.rule.RuleViolation;
import com.bank.rule.TaxRuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxRuleEngine {

    private final TaxRuleRepository taxRuleRepository;
    private final List<TaxRuleValidator> validators;

    public List<Violation> evaluate(TransactionRequest request) {
        FinancialTransaction tx = toTransaction(request);

        List<TaxRule> active = taxRuleRepository.findByEnabledTrue();
        List<Violation> violations = new ArrayList<>();

        for (TaxRule rule : active) {
            TaxRuleValidator validator = validators.stream().filter(v -> v.supports(rule)).findFirst().orElse(null);
            if (validator == null) continue;
            RuleViolation res = validator.validate(tx, rule);
            if (res != null && res.violated()) {
                violations.add(Violation.builder()
                        .ruleCode(rule.getRuleCode())
                        .message(res.message())
                                    .severity(res.severity()==null?null:res.severity().name())
                        .build());
            }
        }

        return violations;
    }

    public void executeRules(FinancialTransaction transaction) {
        // run validations for side-effects (e.g., logging/auditing). Currently just evaluate and ignore return.
        TransactionRequest req = new TransactionRequest(transaction.getAmount(), transaction.getOriginalSaleAmount(), transaction.getTaxRate(), transaction.getTransactionType()==null?null:transaction.getTransactionType().toString());
        evaluate(req);
    }

    private FinancialTransaction toTransaction(TransactionRequest request) {
        FinancialTransaction tx = new FinancialTransaction();
        tx.setAmount(request.amount());
        tx.setOriginalSaleAmount(request.originalSaleAmount());
        tx.setTaxRate(request.taxRate());
        // transactionType left null - this example focuses on numeric validations
        return tx;
    }
}
