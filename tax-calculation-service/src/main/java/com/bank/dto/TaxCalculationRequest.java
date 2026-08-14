package com.bank.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TaxCalculationRequest (

        UUID transactionId,
        UUID customerId,
        BigDecimal amount,
        BigDecimal taxRate,
        BigDecimal reportedTax
) {

}
