package com.bank.dto;

import com.bank.enums.Severity;

public record SeverityCount(
        Severity severity,
        long count
) {
}
