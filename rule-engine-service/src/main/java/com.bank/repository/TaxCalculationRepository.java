package com.bank.repository;

import com.bank.domain.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxCalculationRepository extends JpaRepository<TaxRule, UUID> {
}
