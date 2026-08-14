package com.bank.domain;

import com.bank.enums.ComplianceStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tax_calculations")
@Data
public class TaxCalculation {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "char(36)")
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "transaction_id", unique = true, nullable = false, columnDefinition = "char(36)")
    private UUID transactionId;

    @Column(name = "customer_id", nullable = false, columnDefinition = "char(36)")
    private UUID customerId;

    @Column(precision = 10, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "tax_rate", precision = 10, scale = 4, nullable = false)
    private BigDecimal taxRate;

    @Column(name = "reported_tax", precision = 10, scale = 4)
    private BigDecimal reportedTax;

    @Column(name = "expected_tax", precision = 10, scale = 4)
    private BigDecimal expectedTax;

    @Column(name = "tax_gap", precision = 10, scale = 4)
    private BigDecimal taxGap;

    @Column(name = "compliance_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ComplianceStatus complianceStatus;

    @Column(name = "calculated_date")
    private LocalDate calculatedDate;

}
