package com.bank.controller;

import com.bank.domain.FinancialTransaction;
import com.bank.dto.FinancialTransactionRequest;
import com.bank.dto.TransactionRequest;
import com.bank.dto.Violation;
import com.bank.enums.TransactionType;
import com.bank.service.TaxRuleEngine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class RulEngineController {

    private final TaxRuleEngine taxRuleEngine;

    public RulEngineController(TaxRuleEngine taxRuleEngine) {
        this.taxRuleEngine = taxRuleEngine;
    }

    @GetMapping("/evaluate")
    public ResponseEntity<List<Violation>> evaluate(@RequestBody TransactionRequest transactionRequest){
        List<Violation> evaluate = taxRuleEngine.evaluate(transactionRequest);

        return new ResponseEntity<>(evaluate, HttpStatus.OK);
    }

}
