package com.bank.service;


import com.bank.domain.TaxCalculation;
import com.bank.dto.TaxCalculationRequest;
import com.bank.dto.TaxCalculationResponse;
import com.bank.enums.ComplianceStatus;
import com.bank.repository.TaxCalculationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class TaxCalculationService {

    private final TaxCalculationRepository taxCalculationRepository;

    public TaxCalculationService(TaxCalculationRepository taxCalculationRepository) {
        this.taxCalculationRepository = taxCalculationRepository;
    }

    public TaxCalculationResponse getCalculateTax(UUID transactionId) {
        TaxCalculation taxCalculation = taxCalculationRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Tax calculation not found for transaction ID: " + transactionId));

        return new TaxCalculationResponse(
                taxCalculation.getTaxRate(),
                taxCalculation.getReportedTax(),
                taxCalculation.getExpectedTax(),
                taxCalculation.getTaxGap(),
                taxCalculation.getComplianceStatus()
        );
    }

    private ComplianceStatus determineComplianceStatus(TaxCalculation taxCalculation) {
        // Implement logic to determine compliance status based on tax calculation
        if (taxCalculation.getReportedTax() == null) {
            return ComplianceStatus.NON_COMPLIANT;
        }

        // expectedTax = amount * taxRate
        BigDecimal expectedTax = taxCalculation.getAmount().multiply(taxCalculation.getTaxRate());
        taxCalculation.setExpectedTax(expectedTax);

        // taxGap = expectedTax - reportedTax
        BigDecimal taxGap = expectedTax.subtract(taxCalculation.getReportedTax());
        taxCalculation.setTaxGap(taxGap);

        // Determine compliance: taxGap ≤ 1 → COMPLIANT, taxGap > 1 → UNDERPAID, taxGap < -1 → OVERPAID
        BigDecimal threshold = BigDecimal.ONE;
        BigDecimal absTaxGap = taxGap.abs();

        if (absTaxGap.compareTo(threshold) <= 0) {
            return ComplianceStatus.COMPLIANT;
        } else if (taxGap.compareTo(threshold) > 0) {
            return ComplianceStatus.UNDERPAID;
        } else {
            return ComplianceStatus.OVERPAID;
        }
    }

    public TaxCalculationResponse calculateTax(TaxCalculationRequest taxCalculationRequest) {

        TaxCalculation taxCalculation = new TaxCalculation();
        taxCalculation.setId(UUID.randomUUID());
        taxCalculation.setTransactionId(taxCalculationRequest.transactionId());
        taxCalculation.setCustomerId(taxCalculationRequest.customerId());
        taxCalculation.setAmount(taxCalculationRequest.amount());
        taxCalculation.setTaxRate(taxCalculationRequest.taxRate());
        taxCalculation.setReportedTax(taxCalculationRequest.reportedTax());

        // Calculate expected tax and tax gap
        BigDecimal expectedTax = taxCalculation.getAmount().multiply(taxCalculation.getTaxRate());
        taxCalculation.setExpectedTax(expectedTax);

        BigDecimal taxGap = expectedTax.subtract(taxCalculation.getReportedTax());
        taxCalculation.setTaxGap(taxGap);

        // Determine compliance status
        ComplianceStatus complianceStatus = determineComplianceStatus(taxCalculation);
        taxCalculation.setComplianceStatus(complianceStatus);

        // Set calculated date
        taxCalculation.setCalculatedDate(LocalDate.now());

        // Save to repository
        taxCalculationRepository.save(taxCalculation);

        return new TaxCalculationResponse(
                taxCalculation.getTaxRate(),
                taxCalculation.getReportedTax(),
                taxCalculation.getExpectedTax(),
                taxCalculation.getTaxGap(),
                taxCalculation.getComplianceStatus()
        );

    }
}
