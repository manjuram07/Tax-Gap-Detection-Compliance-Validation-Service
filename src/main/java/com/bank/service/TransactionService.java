//package com.bank.service;
//
//import com.bank.dto.BatchTransactionResponse;
//import com.bank.dto.TransactionRequest;
//import com.bank.dto.TransactionResponse;
//import com.bank.entity.TaxTransaction;
//import com.bank.repository.TaxTransactionRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class TransactionService {
//
//    private final TaxTransactionRepository transactionRepository;
//    private final TaxCalculationService taxCalculationService;
//    private final TaxRuleEngine taxRuleEngine;
//    private final AuditService auditService;
//    private final ObjectMapper objectMapper;
//
//    public BatchTransactionResponse processBatch(
//            List<TransactionRequest> requests) {
//
//        List<TransactionResponse> results = new ArrayList<>();
//
//        for (TransactionRequest request : requests) {
//
//            TaxTransaction transaction =
//                    processSingleTransaction(request);
//
//            results.add(
//                    TransactionResponse.from(transaction)
//            );
//        }
//
//        return new BatchTransactionResponse(results);
//    }
//
//    private TaxTransaction processSingleTransaction(
//            TransactionRequest request) {
//
//        TaxTransaction transaction =
//                new TaxTransaction();
//
//        transaction.setCreatedAt(LocalDate.now());
//
//        try {
//
//            transaction.setTransactionId(
//                    request.transactionId()
//            );
//
//            transaction.setRawPayload(
//                    objectMapper.writeValueAsString(request)
//            );
//
//            auditService.log(
//                    "INGESTION",
//                    transaction,
//                    request
//            );
//
//            validateAndPopulate(request, transaction);
//
//            transaction.setValidationStatus(
//                    ValidationStatus.SUCCESS
//            );
//
//            TaxCalculationResult calculation =
//                    taxCalculationService.calculate(
//                            transaction.getAmount(),
//                            transaction.getTaxRate(),
//                            transaction.getReportedTax()
//                    );
//
//            transaction.setExpectedTax(
//                    calculation.expectedTax()
//            );
//
//            transaction.setTaxGap(
//                    calculation.taxGap()
//            );
//
//            transaction.setComplianceStatus(
//                    calculation.complianceStatus()
//            );
//
//            auditService.log(
//                    "TAX_COMPUTATION",
//                    transaction,
//                    Map.of(
//                            "expectedTax",
//                            calculation.expectedTax(),
//                            "taxGap",
//                            calculation.taxGap(),
//                            "complianceStatus",
//                            calculation.complianceStatus()
//                    )
//            );
//
//            transactionRepository.save(transaction);
//
//            taxRuleEngine.executeRules(transaction);
//
//            return transaction;
//
//        } catch (Exception e) {
//
//            transaction.setValidationStatus(
//                    ValidationStatus.FAILURE
//            );
//
//            transaction.setFailureReason(
//                    e.getMessage()
//            );
//
//            return transactionRepository.save(transaction);
//        }
//    }
//}
