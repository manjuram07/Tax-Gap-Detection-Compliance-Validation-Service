package com.bank.dto;

import java.util.List;

public record ExceptionSummaryResponse(

        long totalExceptions,

        List<SeverityCount> countBySeverity,

        List<CustomerExceptionCount> customerWiseExceptionCount
) {
}
