package com.bank.repository;

import com.bank.entity.TaxTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaxTransactionRepository extends JpaRepository<TaxTransaction, UUID> {

    Optional<TaxTransaction> findByTransactionId(UUID transactionId);

}

