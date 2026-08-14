package com.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RuleExecutionService {

    private final AuditLogService auditLogService;

    public boolean executeRule(UUID transactionId, String ruleName, Map<String, Object> ruleParams) {
        log.info("Executing rule: {} for transactionId: {}", ruleName, transactionId);

        try {
            boolean ruleResult = evaluateRule(ruleName, ruleParams);

            Map<String, Object> ruleDetails = new HashMap<>();
            ruleDetails.put("ruleName", ruleName);
            ruleDetails.put("ruleParameters", ruleParams);
            ruleDetails.put("ruleResult", ruleResult);
            ruleDetails.put("executionStatus", "SUCCESS");

            auditLogService.logRuleExecution(transactionId, ruleDetails);

            log.info("Rule execution completed: {} for transactionId: {}, result: {}", ruleName, transactionId, ruleResult);
            return ruleResult;
        } catch (Exception e) {
            log.error("Error executing rule: {} for transactionId: {}", ruleName, transactionId, e);

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("ruleName", ruleName);
            errorDetails.put("ruleParameters", ruleParams);
            errorDetails.put("executionStatus", "FAILED");
            errorDetails.put("errorMessage", e.getMessage());

            auditLogService.logRuleExecution(transactionId, errorDetails);
            throw new RuntimeException("Rule execution failed", e);
        }
    }

    public void executeRuleSequence(UUID transactionId, String[] ruleNames, String correlationId) {
        log.info("Executing rule sequence for transactionId: {}, correlationId: {}", transactionId, correlationId);

        try {
            Map<String, Object> sequenceDetails = new HashMap<>();
            Map<String, Boolean> results = new HashMap<>();

            for (String ruleName : ruleNames) {
                Map<String, Object> ruleParams = new HashMap<>();
                ruleParams.put("sequenceRule", true);
                
                try {
                    boolean result = evaluateRule(ruleName, ruleParams);
                    results.put(ruleName, result);
                } catch (Exception e) {
                    results.put(ruleName, false);
                    log.error("Rule execution failed in sequence: {}", ruleName, e);
                }
            }

            sequenceDetails.put("ruleNames", ruleNames);
            sequenceDetails.put("ruleResults", results);
            sequenceDetails.put("sequenceStatus", "COMPLETED");

            auditLogService.logEventWithDetails(
                com.bank.enums.EventType.RULE_EXECUTION,
                transactionId,
                sequenceDetails,
                correlationId
            );

            log.info("Rule sequence execution completed for transactionId: {}", transactionId);
        } catch (Exception e) {
            log.error("Error executing rule sequence for transactionId: {}", transactionId, e);
            throw new RuntimeException("Rule sequence execution failed", e);
        }
    }

    private boolean evaluateRule(String ruleName, Map<String, Object> params) {
        return !ruleName.isEmpty() && params != null && !params.isEmpty();
    }
}
