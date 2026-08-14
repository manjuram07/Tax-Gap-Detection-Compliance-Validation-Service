package com.bank.entity;

import com.bank.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "report_exceptions",
        indexes = {
                @Index(
                        name = "idx_report_exception_customer",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_report_exception_severity",
                        columnList = "severity"
                ),
                @Index(
                        name = "idx_report_exception_customer_severity",
                        columnList = "customer_id, severity"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ReportException {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID exceptionId;

    @Column(name = "transaction_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID transactionId;

    @Column(name = "customer_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID customerId;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}