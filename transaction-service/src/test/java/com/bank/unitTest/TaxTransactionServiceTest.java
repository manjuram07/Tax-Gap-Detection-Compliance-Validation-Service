package com.bank.unitTest;

import com.bank.client.TaxCalculationClient;
import com.bank.dto.BatchTransactionResponse;
import com.bank.dto.TaxCalculationResponse;
import com.bank.dto.TaxTransactionRequest;
import com.bank.dto.TaxTransactionResponse;
import com.bank.entity.TaxTransaction;
import com.bank.enums.ComplianceStatus;
import com.bank.enums.TransactionType;
import com.bank.enums.ValidationStatus;
import com.bank.repository.TaxTransactionRepository;
import com.bank.service.TaxTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaxTransactionService Unit Tests")
class TaxTransactionServiceTest {

    @Mock
    private TaxTransactionRepository taxTransactionRepository;

    @Mock
    private TaxCalculationClient taxCalculationClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TaxTransactionService taxTransactionService;

    private UUID transactionId;
    private UUID customerId;
    private TaxTransactionRequest taxTransactionRequest;
    private TaxCalculationResponse taxCalculationResponse;
    private TaxTransaction taxTransaction;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        taxTransactionRequest = new TaxTransactionRequest(
                transactionId,
                LocalDate.now(),
                customerId,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(180),
                TransactionType.SALE
        );

        taxCalculationResponse = new TaxCalculationResponse(
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(180),
                BigDecimal.valueOf(180),
                ComplianceStatus.COMPLIANT
        );

        taxTransaction = new TaxTransaction();
        taxTransaction.setId(UUID.randomUUID());
        taxTransaction.setTransactionId(transactionId);
        taxTransaction.setCustomerId(customerId);
        taxTransaction.setAmount(BigDecimal.valueOf(1000));
        taxTransaction.setTaxRate(BigDecimal.valueOf(0.18));
        taxTransaction.setReportedTax(BigDecimal.valueOf(180));
        taxTransaction.setTransactionType(TransactionType.SALE);
        taxTransaction.setValidationStatus(ValidationStatus.SUCCESS);
        taxTransaction.setDate(LocalDate.now());
        taxTransaction.setCreatedAt(LocalDate.now());
    }

    @Test
    @DisplayName("Should create batch transaction successfully")
    void testCreateBatch_Success() {
        // Arrange
        List<TaxTransactionRequest> requests = Collections.singletonList(taxTransactionRequest);
        when(taxCalculationClient.getTaxCalculation(transactionId)).thenReturn(taxCalculationResponse);
        when(taxTransactionRepository.save(any(TaxTransaction.class))).thenReturn(taxTransaction);

        // Act
        BatchTransactionResponse response = taxTransactionService.createBatch(requests);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.totalTransactions());
        assertEquals(1, response.successfulTransactions());
        assertEquals(0, response.failedTransactions());
        assertEquals(1, response.transactions().size());
        verify(taxCalculationClient, times(1)).getTaxCalculation(transactionId);
        verify(taxTransactionRepository, times(1)).save(any(TaxTransaction.class));
    }

    @Test
    @DisplayName("Should create batch with multiple transactions")
    void testCreateBatch_MultipleTransactions() {
        // Arrange
        UUID transactionId2 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();

        TaxTransactionRequest request2 = new TaxTransactionRequest(
                transactionId2,
                LocalDate.now(),
                customerId2,
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(360),
                TransactionType.REFUND
        );

        List<TaxTransactionRequest> requests = Arrays.asList(taxTransactionRequest, request2);

        when(taxCalculationClient.getTaxCalculation(any(UUID.class))).thenReturn(taxCalculationResponse);
        when(taxTransactionRepository.save(any(TaxTransaction.class))).thenReturn(taxTransaction);

        // Act
        BatchTransactionResponse response = taxTransactionService.createBatch(requests);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.totalTransactions());
        assertEquals(2, response.successfulTransactions());
        assertEquals(0, response.failedTransactions());
        verify(taxCalculationClient, times(2)).getTaxCalculation(any(UUID.class));
        verify(taxTransactionRepository, times(2)).save(any(TaxTransaction.class));
    }

    @Test
    @DisplayName("Should get all tax transactions")
    void testGetAllTaxTransactions_Success() {
        // Arrange
        List<TaxTransaction> transactions = Collections.singletonList(taxTransaction);
        when(taxTransactionRepository.findAll()).thenReturn(transactions);

        // Act
        List<TaxTransactionResponse> responses = taxTransactionService.getAllTaxTransactions();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(transactionId, responses.get(0).transactionId());
        verify(taxTransactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no transactions exist")
    void testGetAllTaxTransactions_EmptyList() {
        // Arrange
        when(taxTransactionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<TaxTransactionResponse> responses = taxTransactionService.getAllTaxTransactions();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(taxTransactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get tax transaction by ID successfully")
    void testGetTaxTransaction_Success() {
        // Arrange
        when(taxTransactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(taxTransaction));

        // Act
        TaxTransactionResponse response = taxTransactionService.getTaxTransaction(transactionId);

        // Assert
        assertNotNull(response);
        assertEquals(transactionId, response.transactionId());
        assertEquals(customerId, response.customerId());
        assertEquals(ValidationStatus.SUCCESS, response.validationStatus());
        verify(taxTransactionRepository, times(1)).findByTransactionId(transactionId);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when transaction not found")
    void testGetTaxTransaction_NotFound() {
        // Arrange
        when(taxTransactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> taxTransactionService.getTaxTransaction(transactionId));
        verify(taxTransactionRepository, times(1)).findByTransactionId(transactionId);
    }

    @Test
    @DisplayName("Should handle transaction with validation failure")
    void testCreateBatch_WithValidationFailure() {
        // Arrange
        TaxTransaction failedTransaction = new TaxTransaction();
        failedTransaction.setId(UUID.randomUUID());
        failedTransaction.setTransactionId(transactionId);
        failedTransaction.setCustomerId(customerId);
        failedTransaction.setAmount(BigDecimal.valueOf(1000));
        failedTransaction.setValidationStatus(ValidationStatus.FAILURE);
        failedTransaction.setFailureReason("Validation failed");
        failedTransaction.setCreatedAt(LocalDate.now());

        List<TaxTransactionRequest> requests = Collections.singletonList(taxTransactionRequest);
        when(taxCalculationClient.getTaxCalculation(transactionId)).thenReturn(taxCalculationResponse);
        when(taxTransactionRepository.save(any(TaxTransaction.class))).thenReturn(failedTransaction);

        // Act
        BatchTransactionResponse response = taxTransactionService.createBatch(requests);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.totalTransactions());
        verify(taxTransactionRepository, times(1)).save(any(TaxTransaction.class));
    }

    @Test
    @DisplayName("Should handle empty batch request")
    void testCreateBatch_EmptyList() {
        // Arrange
        List<TaxTransactionRequest> requests = Collections.emptyList();

        // Act
        BatchTransactionResponse response = taxTransactionService.createBatch(requests);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.totalTransactions());
        assertEquals(0, response.successfulTransactions());
        assertEquals(0, response.failedTransactions());
        verify(taxTransactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle null tax calculation response")
    void testCreateBatch_NullTaxCalculationResponse() {
        // Arrange
        List<TaxTransactionRequest> requests = Collections.singletonList(taxTransactionRequest);
        when(taxCalculationClient.getTaxCalculation(transactionId)).thenReturn(
                new TaxCalculationResponse(null, null, null, null)
        );
        when(taxTransactionRepository.save(any(TaxTransaction.class))).thenReturn(taxTransaction);

        // Act & Assert
        assertDoesNotThrow(() -> taxTransactionService.createBatch(requests));
        verify(taxCalculationClient, times(1)).getTaxCalculation(transactionId);
    }
}
