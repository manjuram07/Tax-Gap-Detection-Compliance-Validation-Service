package com.bank.repository;

import com.bank.domain.TaxCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxCalculationRepository extends JpaRepository<TaxCalculation, UUID> {
}
