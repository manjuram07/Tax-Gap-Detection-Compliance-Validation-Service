package com.bank.dto;

import com.bank.entity.TaxTransaction;
import com.bank.enums.ValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TaxTransactionResponse(
        UUID id,
        UUID transactionId,
        UUID customerId,
        ValidationStatus validationStatus,
        String failureReason,
        BigDecimal reportedTax,
        LocalDate createdDate,
        BigDecimal taxRate
) {

    public static TaxTransactionResponse from(
            TaxTransaction taxTransaction) {

        return new TaxTransactionResponse(
                taxTransaction.getId(),
                taxTransaction.getTransactionId(),
                taxTransaction.getCustomerId(),
                taxTransaction.getValidationStatus(),
                taxTransaction.getFailureReason(),
                taxTransaction.getReportedTax(),
                taxTransaction.getDate(),
                taxTransaction.getTaxRate()
        );
    }
}