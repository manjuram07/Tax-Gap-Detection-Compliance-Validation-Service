package com.bank.repository;

import com.bank.domain.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FinancialTransactionRepository
        extends JpaRepository<FinancialTransaction, UUID> {

    Optional<FinancialTransaction> findByTransactionId(
            String transactionId
    );
}