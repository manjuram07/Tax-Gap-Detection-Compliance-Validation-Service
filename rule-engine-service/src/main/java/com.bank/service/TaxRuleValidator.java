package com.bank.service;

import com.bank.domain.FinancialTransaction;
import com.bank.domain.TaxRule;

public interface TaxRuleValidator {
    boolean supports(TaxRule taxRule);
    com.bank.service.RuleViolation validate(FinancialTransaction transaction, TaxRule taxRule);
}
