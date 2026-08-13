package com.bank.entity;

import com.bank.enums.TransactionType;
import com.bank.enums.ValidationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "tax_transactions"
//        indexes = {
//                @Index(name = "idx_tx_customer", columnList = "customer_id"),
//                @Index(name = "idx_tx_date", columnList = "transaction_date"),
//                @Index(name = "idx_tx_compliance", columnList = "compliance_status")
//        }
)
@Getter
@Setter
@Data
public class TaxTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private UUID transactionId;

    @Column(name = "transaction_date")
    private LocalDate date;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "tax_rate", precision = 10, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "reported_tax", precision = 19, scale = 4)
    private BigDecimal reportedTax;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false)
    private ValidationStatus validationStatus;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
    }
}
