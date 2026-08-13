package com.bank.domain;

import com.bank.enums.ComplianceStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tax_calculations")
@Data
public class TaxCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private UUID transactionId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(precision = 10, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "tax_rate", precision = 10, scale = 4, nullable = false)
    private BigDecimal taxRate;

    @Column(name = "reported_tax", precision = 10, scale = 4)
    private BigDecimal reportedTax;

    @Column(name = "expected_tax", precision = 10, scale = 4)
    private BigDecimal expectedTax;

    @Column
    private  Integer taxGap;

    @Column(name = "compliance_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ComplianceStatus compliance_status;

    @Column(name = "calculated_date")
    private LocalDate calculatedDate;

}
