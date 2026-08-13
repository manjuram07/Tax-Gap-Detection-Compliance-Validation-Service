package com.bank.validator;

import com.bank.dto.TransactionRequest;
import com.bank.entity.TaxTransaction;
import com.bank.enums.TransactionType;
import com.bank.enums.ValidationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Slf4j
@Component
public class TransactionValidator {

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE,           // yyyy-MM-dd
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),  // dd/MM/yyyy
        DateTimeFormatter.ofPattern("MM/dd/yyyy")   // MM/dd/yyyy
    };

    public TaxTransaction validate(TransactionRequest request) {
        TaxTransaction transaction = new TaxTransaction();
        transaction.setValidationStatus(ValidationStatus.SUCCESS);
        transaction.setRawPayload(request.toString());

        // Validate required fields
        if (!isFieldPresent(request.transactionId())) {
            setValidationFailure(transaction, "Missing required field: transactionId");
            return transaction;
        }

        if (!isFieldPresent(request.date())) {
            setValidationFailure(transaction, "Missing required field: date");
            return transaction;
        }

        if (!isFieldPresent(request.customerId())) {
            setValidationFailure(transaction, "Missing required field: customerId");
            return transaction;
        }

        if (!isFieldPresent(request.amount())) {
            setValidationFailure(transaction, "Missing required field: amount");
            return transaction;
        }

        if (!isFieldPresent(request.transactionType())) {
            setValidationFailure(transaction, "Missing required field: transactionType");
            return transaction;
        }

        transaction.setTransactionId(request.transactionId());

        // Validate and parse date
        LocalDate parsedDate = parseDate(request.date());
        if (parsedDate == null) {
            setValidationFailure(transaction, "Invalid date format: " + request.date() + ". Expected formats: yyyy-MM-dd, dd/MM/yyyy, MM/dd/yyyy");
            return transaction;
        }
        transaction.setDate(parsedDate);

        // Validate and parse customerId
        try {
            UUID customerId = UUID.fromString(request.customerId());
            transaction.setCustomerId(customerId);
        } catch (IllegalArgumentException e) {
            setValidationFailure(transaction, "Invalid customerId format: " + request.customerId());
            return transaction;
        }

        // Validate and parse amount
        BigDecimal amount = parseAmount(request.amount());
        if (amount == null) {
            setValidationFailure(transaction, "Invalid amount format: " + request.amount());
            return transaction;
        }
        if (amount.signum() <= 0) {
            setValidationFailure(transaction, "Amount must be greater than 0, got: " + amount);
            return transaction;
        }
        transaction.setAmount(amount);

        // Validate and parse taxRate (optional)
        if (isFieldPresent(request.taxRate())) {
            BigDecimal taxRate = parseAmount(request.taxRate());
            if (taxRate == null) {
                setValidationFailure(transaction, "Invalid taxRate format: " + request.taxRate());
                return transaction;
            }
            if (taxRate.signum() < 0) {
                setValidationFailure(transaction, "Tax rate cannot be negative, got: " + taxRate);
                return transaction;
            }
            transaction.setTaxRate(taxRate);
        }

        // Validate and parse reportedTax (optional)
        if (isFieldPresent(request.reportedTax())) {
            BigDecimal reportedTax = parseAmount(request.reportedTax());
            if (reportedTax == null) {
                setValidationFailure(transaction, "Invalid reportedTax format: " + request.reportedTax());
                return transaction;
            }
            if (reportedTax.signum() < 0) {
                setValidationFailure(transaction, "Reported tax cannot be negative, got: " + reportedTax);
                return transaction;
            }
            transaction.setReportedTax(reportedTax);
        }

        // Validate and parse transactionType
        try {
            TransactionType txType = TransactionType.valueOf(request.transactionType().toUpperCase());
            transaction.setTransactionType(txType);
        } catch (IllegalArgumentException e) {
            setValidationFailure(transaction, "Invalid transactionType: " + request.transactionType() + ". Valid types: SALE, REFUND, EXPENSE");
            return transaction;
        }

        return transaction;
    }

    private boolean isFieldPresent(String field) {
        return field != null && !field.trim().isEmpty();
    }

    private LocalDate parseDate(String dateString) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateString.trim(), formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        log.warn("Failed to parse date: {}", dateString);
        return null;
    }

    private BigDecimal parseAmount(String amountString) {
        try {
            return new BigDecimal(amountString.trim());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse amount: {}", amountString);
            return null;
        }
    }

    private void setValidationFailure(TaxTransaction transaction, String reason) {
        transaction.setValidationStatus(ValidationStatus.FAILURE);
        transaction.setFailureReason(reason);
        log.error("Transaction validation failed: {}", reason);
    }
}
