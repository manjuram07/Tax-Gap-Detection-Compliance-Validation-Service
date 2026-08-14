package com.bank.domain;

import com.bank.enums.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "transaction_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID transactionId;

    @Column(name = "event_timestamp", nullable = false)
    private OffsetDateTime eventTimestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail_json", columnDefinition = "JSON")
    private JsonNode detailJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tax_details", columnDefinition = "JSON")
    private TaxDetails taxDetails;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (eventTimestamp == null) {
            eventTimestamp = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }
    }
}