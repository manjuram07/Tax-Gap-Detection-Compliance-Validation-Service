package com.bank.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomerTaxSummaryResponse(

        UUID customerId,

        BigDecimal totalAmount,

        BigDecimal totalReportedTax,

        BigDecimal totalExpectedTax,

        BigDecimal totalTaxGap,

        long totalTransactions,

        long nonCompliantTransactions,

        BigDecimal complianceScore
) {
}
