package com.bank.rule;

import com.bank.enums.Severity;

public record RuleViolation(
        boolean violated,
        Severity severity,
        String message
) {

    public static RuleViolation pass() {
        return new RuleViolation(
                false,
                null,
                null
        );
    }

    public static RuleViolation fail(
            Severity severity,
            String message
    ) {
        return new RuleViolation(
                true,
                severity,
                message
        );
    }
}
