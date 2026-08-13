package com.bank.dto;

import com.bank.entity.TaxTransaction;
import com.bank.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        String transactionId,
        BigDecimal amount,
        TransactionType transactionType,
        LocalDate createdAt

) {
    public static TransactionResponse from(TaxTransaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getCreatedAt()
        );
    }
}
