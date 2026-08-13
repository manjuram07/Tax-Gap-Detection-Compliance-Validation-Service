package com.bank.controller;


import com.bank.dto.TaxTransactionRequest;
import com.bank.dto.TaxTransactionResponse;
import com.bank.service.TaxTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public TaxTransactionResponse createTaxTransaction(@RequestBody List<TaxTransactionRequest> taxTransactionRequest) {
        log.info("Received transaction request: {}", taxTransactionRequest);
        taxTransactionService.createBatch(taxTransactionRequest);
        return null;
    }
}
