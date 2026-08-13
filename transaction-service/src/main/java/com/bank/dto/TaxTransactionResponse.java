package com.bank.dto;

import com.bank.entity.TaxTransaction;
import com.bank.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public record TaxTransactionResponse(
        UUID transactionId,
        BigDecimal amount,
        TransactionType transactionType,
        LocalDate createdAt

) {
    public static TaxTransactionResponse from(TaxTransactionResponse transaction) {
        return new TaxTransactionResponse(
                transaction.transactionId(),
                transaction.amount(),
                transaction.transactionType(),
                transaction.createdAt()
        );
    }
}
