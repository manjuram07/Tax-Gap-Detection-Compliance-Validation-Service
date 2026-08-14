package com.bank.dto;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        BigDecimal originalSaleAmount,
        BigDecimal taxRate,
        String type
) {
}
