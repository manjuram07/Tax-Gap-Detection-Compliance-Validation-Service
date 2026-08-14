package com.bank.dto;

import com.bank.enums.TransactionType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TaxTransactionRequest(

        @NotBlank(message = "transactionId is required")
        UUID transactionId,

        @NotBlank(message = "date is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,

        @NotBlank(message = "customerId is required")
        UUID customerId,

        @NotBlank(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        BigDecimal amount,

        BigDecimal taxRate,

        BigDecimal reportedTax,

        @NotBlank(message = "transactionType is required")
        TransactionType transactionType
) { }
