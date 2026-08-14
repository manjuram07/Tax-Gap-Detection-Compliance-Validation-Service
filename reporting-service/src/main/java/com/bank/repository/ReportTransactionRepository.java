package com.bank.repository;

import com.bank.entity.ReportTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportTransactionRepository extends JpaRepository<ReportTransaction, UUID> {
}
