package com.bank.dto;

import com.bank.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionReportRequest(

        UUID transactionId,

        LocalDate transactionDate,

        UUID customerId,

        BigDecimal amount,

        BigDecimal expectedTax,

        BigDecimal reportedTax,

        TransactionType transactionType
) {
}
