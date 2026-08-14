//package com.bank.service;
//
//import com.bank.domain.FinancialTransaction;
//import com.bank.domain.TaxException;
//import com.bank.domain.TaxRule;
//import com.bank.repository.TaxExceptionRepository;
//import com.bank.repository.TaxRuleRepository;
//import com.bank.rule.RuleViolation;
//import com.bank.rule.TaxRuleValidator;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class RuleEngineService {
//
//    private final TaxRuleRepository taxRuleRepository;
//
//    private final TaxExceptionRepository taxExceptionRepository;
//
//    private final List<TaxRuleValidator> validators;
//
//    @Transactional
//    public void executeRules(
//            FinancialTransaction transaction) {
//
//        List<TaxRule> activeRules =
//                taxRuleRepository.findByEnabledTrue();
//
//        for (TaxRule rule : activeRules) {
//
//            TaxRuleValidator validator =
//                    findValidator(rule);
//
//            RuleViolation result =
//                    validator.validate(
//                            transaction,
//                            rule
//                    );
//
//            if (result.violated()) {
//
//                createException(
//                        transaction,
//                        rule,
//                        result
//                );
//            }
//        }
//    }
//
//    private TaxRuleValidator findValidator(
//            TaxRule taxRule) {
//
//        return validators.stream()
//                .filter(
//                        validator ->
//                                validator.supports(taxRule)
//                )
//                .findFirst()
//                .orElseThrow(
//                        () -> new IllegalStateException(
//                                "No validator found for rule type: "
//                                        + taxRule.getRuleType()
//                        )
//                );
//    }
//
//    private void createException(
//            FinancialTransaction transaction,
//            TaxRule rule,
//            RuleViolation violation) {
//
//        TaxException exception =
//                TaxException.builder()
//                        .transactionId(
//                                transaction.getTransactionId()
//                        )
//                        .customerId(
//                                transaction.getCustomerId()
//                        )
//                        .ruleName(
//                                rule.getRuleName()
//                        )
//                        .severity(
//                                violation.severity()
//                        )
//                        .message(
//                                violation.message()
//                        )
//                        .build();
//
//        taxExceptionRepository.save(exception);
//    }
//}