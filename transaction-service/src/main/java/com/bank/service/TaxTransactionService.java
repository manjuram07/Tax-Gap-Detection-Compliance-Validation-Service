package com.bank.service;

import com.bank.dto.BatchTransactionResponse;
import com.bank.dto.TaxTransactionRequest;
import com.bank.dto.TaxTransactionResponse;
import com.bank.entity.TaxTransaction;
import com.bank.repository.TaxTransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaxTransactionService {

    private final TaxTransactionRepository taxTransactionRepository;
    private final ObjectMapper objectMapper;

    public TaxTransactionService(TaxTransactionRepository taxTransactionRepository, ObjectMapper objectMapper) {
        this.taxTransactionRepository = taxTransactionRepository;
        this.objectMapper=objectMapper;
    }


    public BatchTransactionResponse createBatch(List<TaxTransactionRequest> taxTransactionRequest) {

        List<TaxTransactionResponse> responses = new ArrayList<>();


        for (TaxTransactionRequest request : taxTransactionRequest) {
            TaxTransactionResponse taxTransactionResponse = createTransaction(request);
            responses.add(TaxTransactionResponse.from(taxTransactionResponse));
        }

        return null;
    }

    private TaxTransactionResponse createTransaction(TaxTransactionRequest request) {

        TaxTransaction transaction = new TaxTransaction();

        transaction.setCreatedAt(LocalDate.now());



            return null;
        }

    }