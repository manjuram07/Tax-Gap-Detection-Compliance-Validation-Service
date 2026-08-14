package com.bank.controller;


import com.bank.dto.BatchTransactionResponse;
import com.bank.dto.TaxTransactionRequest;
import com.bank.dto.TaxTransactionResponse;
import com.bank.service.TaxTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class TaxTransactionController {

    private TaxTransactionService taxTransactionService;

    public TaxTransactionController(TaxTransactionService taxTransactionService) {
        this.taxTransactionService = taxTransactionService;
    }

    @PostMapping("/transactions/batch")
    public ResponseEntity<BatchTransactionResponse> createTaxTransaction(@RequestBody List<TaxTransactionRequest> taxTransactionRequest) {
        log.info("Received transaction request: {}", taxTransactionRequest);
        BatchTransactionResponse response = taxTransactionService.createBatch(taxTransactionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TaxTransactionResponse>> getAllTaxTransactions() {
        List<TaxTransactionResponse> transactions = taxTransactionService.getAllTaxTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
