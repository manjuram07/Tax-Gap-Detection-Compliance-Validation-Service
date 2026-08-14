package com.bank.service;

import com.bank.client.TaxCalculationClient;
import com.bank.dto.BatchTransactionResponse;
import com.bank.dto.TaxCalculationResponse;
import com.bank.dto.TaxTransactionRequest;
import com.bank.dto.TaxTransactionResponse;
import com.bank.entity.TaxTransaction;
import com.bank.enums.ValidationStatus;
import com.bank.repository.TaxTransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TaxTransactionService {

    private final TaxTransactionRepository taxTransactionRepository;
    private final ObjectMapper objectMapper;
    private final TaxCalculationClient taxCalculationClient;

    public TaxTransactionService(TaxTransactionRepository taxTransactionRepository, ObjectMapper objectMapper, TaxCalculationClient taxCalculationClient) {
        this.taxTransactionRepository = taxTransactionRepository;
        this.objectMapper = objectMapper;
        this.taxCalculationClient = taxCalculationClient;
    }


    public BatchTransactionResponse createBatch(List<TaxTransactionRequest> taxTransactionRequest) {

        List<TaxTransactionResponse> responses = new ArrayList<>();

        for (TaxTransactionRequest request : taxTransactionRequest) {
            TaxTransactionResponse taxTransactionResponse = createTransaction(request);
            responses.add(taxTransactionResponse);
        }

        return new BatchTransactionResponse(
                taxTransactionRequest.size(),
                (int) responses.stream().filter(r -> r.validationStatus() == ValidationStatus.SUCCESS).count(),
                (int) responses.stream().filter(r -> r.validationStatus() == ValidationStatus.FAILURE).count(),
                responses
        );
    }

    private TaxTransactionResponse createTransaction(TaxTransactionRequest request) {

        TaxTransaction transaction = new TaxTransaction();

        transaction.setCreatedAt(LocalDate.now());
        transaction.setDate(request.date());
        transaction.setTransactionId(request.transactionId());
        transaction.setAmount(request.amount());
        transaction.setTransactionType(request.transactionType());
        transaction.setCustomerId(request.customerId());

        TaxCalculationResponse taxCalculation = taxCalculationClient.getTaxCalculation(request.transactionId());

        transaction.setReportedTax(taxCalculation.reportedTax());
        transaction.setTaxRate(taxCalculation.taxRate());

        if(transaction.getValidationStatus() == ValidationStatus.FAILURE) {;
            transaction.setFailureReason("Validation failed for transaction: " + transaction.getTransactionId());
        } else {
            transaction.setValidationStatus(ValidationStatus.SUCCESS);
        }

        taxTransactionRepository.save(transaction);

        return TaxTransactionResponse.from(transaction);
    }

    public List<TaxTransactionResponse> getAllTaxTransactions() {
        List<TaxTransaction> transactions = taxTransactionRepository.findAll();
        return transactions.stream()
                .map(TaxTransactionResponse::from)
                .toList();
    }

    public TaxTransactionResponse getTaxTransaction(UUID transactionId){

        Optional<TaxTransaction> transaction = taxTransactionRepository.findByTransactionId(transactionId);
        if (transaction.isEmpty()) {
            throw new NoSuchElementException("No Transaction found with ID: " + transactionId);
        }
        return TaxTransactionResponse.from(transaction.get());
    }
}