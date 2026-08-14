package com.bank.rule;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;

public interface TaxRuleValidator {

    boolean supports(TaxRule taxRule);

    RuleViolation validate(
            FinancialTransaction transaction,
            TaxRule taxRule
    );
}
