package com.bank.dto;

import java.math.BigDecimal;

public record FinancialTransactionRequest(
        String transactionId,
        String customerId,
        String transactionType,
        BigDecimal amount,
        BigDecimal originalSaleAmount,
        BigDecimal taxRate,
        BigDecimal reportedTax
) {
}
