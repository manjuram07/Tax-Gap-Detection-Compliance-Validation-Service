package com.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxDetails {

    private String taxType;
    private BigDecimal amount;
    private BigDecimal calculatedTax;
    private String taxStatus;
    private String description;
}
