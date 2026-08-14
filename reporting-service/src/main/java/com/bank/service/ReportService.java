package com.bank.service;

import com.bank.dto.CustomerTaxSummaryResponse;
import com.bank.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public CustomerTaxSummaryResponse getCustomerTaxSummary(UUID customerId) {

        return reportRepository.getCustomerTaxSummary(customerId);
    }
}
