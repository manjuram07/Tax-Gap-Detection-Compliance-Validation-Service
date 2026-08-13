package com.bank.dto;

import java.util.List;

public record BatchTransactionResponse(
        int totalTransactions,
        int successfulTransactions,
        int failedTransactions,
        List<TaxTransactionResponse> transactions
) {
}