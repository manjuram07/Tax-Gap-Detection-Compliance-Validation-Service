package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

public record TaxTransactionRequest(

        @NotBlank(message = "transactionId is required")
        UUID transactionId,

        @NotBlank(message = "date is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,

        @NotBlank(message = "customerId is required")
        String customerId,

        @NotBlank(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        String amount,

        String taxRate,

        String reportedTax,

        @NotBlank(message = "transactionType is required")
        String transactionType
) { }
