package com.bank.service;

import com.bank.dto.TransactionReportRequest;
import com.bank.entity.ReportTransaction;
import com.bank.repository.ReportTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportTransactionService {

    private final ReportTransactionRepository repository;

    @Transactional
    public void save(TransactionReportRequest request) {

        ReportTransaction transaction =
                new ReportTransaction();

        transaction.setTransactionId(request.transactionId()
        );

        transaction.setCustomerId(
                request.customerId()
        );

        transaction.setAmount(
                request.amount()
        );

        transaction.setReportedTax(
                request.reportedTax()
        );

        transaction.setExpectedTax(
                request.expectedTax()
        );

        transaction.setTransactionType(
                request.transactionType()
        );

        transaction.setTransactionDate(
                request.transactionDate()
        );

        transaction.setUpdatedAt(
                LocalDate.now()
        );

        repository.save(transaction);
    }
}
