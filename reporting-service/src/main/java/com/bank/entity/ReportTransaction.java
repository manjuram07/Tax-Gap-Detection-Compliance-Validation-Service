package com.bank.entity;

import com.bank.enums.TransactionType;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(
        name = "report_transactions",
        indexes = {
                @Index(
                        name = "idx_report_tx_customer",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_report_tx_compliance",
                        columnList = "compliance_status"
                ),
                @Index(
                        name = "idx_report_tx_customer_compliance",
                        columnList = "customer_id, compliance_status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ReportTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID transactionId;

    @Column(name = "customer_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID customerId;

    @Column(
            precision = 19,
            scale = 4,
            nullable = false
    )
    private BigDecimal amount;

    @Column(
            name = "reported_tax",
            precision = 19,
            scale = 4
    )
    private BigDecimal reportedTax;

    @Column(
            name = "expected_tax",
            precision = 19,
            scale = 4
    )
    private BigDecimal expectedTax;

    @Column(
            name = "tax_gap",
            precision = 19,
            scale = 4
    )
    private BigDecimal taxGap;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false
    )
    private TransactionType transactionType;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;
}