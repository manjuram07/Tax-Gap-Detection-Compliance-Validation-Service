package com.bank.repository;

import com.bank.domain.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaxRuleRepository extends JpaRepository<TaxRule, UUID> {

    List<TaxRule> findByEnabledTrue();
}
