package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(

        @NotBlank(message = "transactionId is required")
        String transactionId,

        @NotBlank(message = "date is required")
        String date,

        @NotBlank(message = "customerId is required")
        String customerId,

        @NotBlank(message = "amount is required")
        String amount,

        String taxRate,

        String reportedTax,

        @NotBlank(message = "transactionType is required")
        String transactionType
) {
}
