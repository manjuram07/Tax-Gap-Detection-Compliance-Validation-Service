package com.bank.dto;

import java.math.BigDecimal;

public record TaxDetails(
         BigDecimal amount,
         BigDecimal taxRate,
         BigDecimal reportedTax,
         BigDecimal expectedTax,
         Integer taxGap,
         ComplianceStatus complianceStatus
) { }
