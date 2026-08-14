package com.bank.controller;


import com.bank.dto.CustomerTaxSummaryResponse;
import com.bank.dto.TransactionReportRequest;
import com.bank.service.ReportService;
import com.bank.service.ReportTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportTransactionService reportTransactionService;

    @GetMapping("/customers/{customerId}/tax-summary")
    public ResponseEntity<CustomerTaxSummaryResponse> getCustomerTaxSummary(@PathVariable UUID customerId) {

        return ResponseEntity.ok(reportService.getCustomerTaxSummary(customerId));
    }

    @PostMapping("/report")
    public ResponseEntity<Void> createReportTransaction(@Valid @RequestBody TransactionReportRequest request) {

        reportTransactionService.save(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}