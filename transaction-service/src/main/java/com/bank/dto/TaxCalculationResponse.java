package com.bank.dto;

import com.bank.enums.ComplianceStatus;

import java.math.BigDecimal;

public record TaxCalculationResponse(
    BigDecimal taxRate,
    BigDecimal reportedTax,
    BigDecimal expectedTax,
    ComplianceStatus compliance_status
) { }
