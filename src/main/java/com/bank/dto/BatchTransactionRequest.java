package com.bank.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchTransactionRequest(
        @NotEmpty
        List<TransactionRequest> transactions
) {
}