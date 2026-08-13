package com.bank.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tax_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tax_rules_rule_code",
                        columnNames = "rule_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "rule_code", nullable = false, unique = true, length = 100)
    private String ruleCode;

    @Column(name = "rule_name", nullable = false, length = 255)
    private String ruleName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "rule_type", nullable = false, length = 100)
    private String ruleType;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}