package com.bank.controller;

import com.bank.dto.TaxCalculationRequest;
import com.bank.dto.TaxCalculationResponse;
import com.bank.service.TaxCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TaxCalculationController {

    private final TaxCalculationService taxCalculationService;

    public TaxCalculationController(TaxCalculationService taxCalculationService) {
        this.taxCalculationService = taxCalculationService;
    }

    @GetMapping("/{transactionId}")
    public TaxCalculationResponse getCalculateTax(@PathVariable("transactionId") UUID transactionId) {
        return taxCalculationService.getCalculateTax(transactionId);
    }


    @PostMapping("/calculate-tax")
    public ResponseEntity<TaxCalculationResponse> calculateTaxPost(@RequestBody TaxCalculationRequest request) {
        TaxCalculationResponse taxCalculationResponse = taxCalculationService.calculateTax(request);
        return ResponseEntity.ok(taxCalculationResponse);
    }
}
