package com.bank.entity;

import jakarta.persistence.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class ValidationRule {

    private String transactionId;

    private LocalDate date;

    private BigDecimal amount;

    private String customerId;

    private String transactionType;
}
