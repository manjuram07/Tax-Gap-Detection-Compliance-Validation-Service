package com.bank.domain;


import com.bank.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table
public class FinancialTransaction {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @Column
    private UUID transactionId;

    @Column
    private UUID customerId;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(precision = 10, scale = 4)
    private BigDecimal amount;

    @Column(precision = 10, scale = 4)
    private BigDecimal originalSaleAmount;

    @Column(precision = 10, scale = 4)
    private BigDecimal taxRate;

    @Column(precision = 10, scale = 4)
    private BigDecimal reportedTax;
}
