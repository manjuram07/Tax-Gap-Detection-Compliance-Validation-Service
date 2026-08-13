package com.bank.repository;

import com.bank.entity.TaxTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxTransactionRepository extends JpaRepository<TaxTransaction, Long> {

}

