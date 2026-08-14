package com.bank.integrationTest;

import com.bank.dto.BatchTransactionResponse;
import com.bank.dto.TaxTransactionRequest;
import com.bank.dto.TaxTransactionResponse;
import com.bank.entity.TaxTransaction;
import com.bank.enums.TransactionType;
import com.bank.enums.ValidationStatus;
import com.bank.repository.TaxTransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("TaxTransactionController Integration Tests")
@Transactional
class TaxTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaxTransactionRepository taxTransactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID transactionId;
    private UUID customerId;
    private TaxTransactionRequest taxTransactionRequest;

    @BeforeEach
    void setUp() {
        taxTransactionRepository.deleteAll();
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
    }

    @Test
    @DisplayName("Should create batch transactions and return 201 CREATED")
    void testCreateBatchTransaction_Success() throws Exception {
        // Arrange
        List<TaxTransactionRequest> requests = Collections.singletonList(taxTransactionRequest);
        String requestBody = objectMapper.writeValueAsString(requests);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/v1/transactions/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalTransactions", is(1)))
                .andExpect(jsonPath("$.successfulTransactions", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.failedTransactions", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.transactions", hasSize(1)))
                .andReturn();

        BatchTransactionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BatchTransactionResponse.class
        );

        assertNotNull(response);
        assertEquals(1, response.totalTransactions());
    }

    @Test
    @DisplayName("Should create batch with multiple transactions")
    void testCreateBatchTransaction_Multiple() throws Exception {
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
        String requestBody = objectMapper.writeValueAsString(requests);

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalTransactions", is(2)))
                .andExpect(jsonPath("$.transactions", hasSize(2)))
                .andReturn();
    }

    @Test
    @DisplayName("Should get all transactions and return 200 OK")
    void testGetAllTransactions_Success() throws Exception {
        // Arrange - Create a transaction first
        TaxTransaction transaction = new TaxTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setCustomerId(customerId);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTaxRate(BigDecimal.valueOf(0.18));
        transaction.setReportedTax(BigDecimal.valueOf(180));
        transaction.setTransactionType(TransactionType.SALE);
        transaction.setValidationStatus(ValidationStatus.SUCCESS);
        transaction.setDate(LocalDate.now());
        transaction.setCreatedAt(LocalDate.now());
        taxTransactionRepository.save(transaction);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(Collection.class)))
                .andExpect(jsonPath("$[0].transactionId", is(transactionId.toString())))
                .andExpect(jsonPath("$[0].customerId", is(customerId.toString())))
                .andExpect(jsonPath("$[0].validationStatus", is(ValidationStatus.SUCCESS.toString())))
                .andReturn();
    }

    @Test
    @DisplayName("Should return empty list when no transactions exist")
    void testGetAllTransactions_Empty() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();
    }

    @Test
    @DisplayName("Should get transaction by ID and return 200 OK")
    void testGetTransactionById_Success() throws Exception {
        // Arrange - Create a transaction first
        TaxTransaction transaction = new TaxTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setCustomerId(customerId);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTaxRate(BigDecimal.valueOf(0.18));
        transaction.setReportedTax(BigDecimal.valueOf(180));
        transaction.setTransactionType(TransactionType.SALE);
        transaction.setValidationStatus(ValidationStatus.SUCCESS);
        transaction.setCreatedAt(LocalDate.now());
        taxTransactionRepository.save(transaction);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId", is(transactionId.toString())))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())))
                .andExpect(jsonPath("$.validationStatus", is(ValidationStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$.amount").doesNotExist()) // Check field that doesn't exist in response
                .andReturn();
    }

    @Test
    @DisplayName("Should persist transaction to database")
    void testCreateBatchTransaction_PersistenceCheck() throws Exception {
        // Arrange
        List<TaxTransactionRequest> requests = Collections.singletonList(taxTransactionRequest);
        String requestBody = objectMapper.writeValueAsString(requests);

        // Act
        mockMvc.perform(post("/api/v1/transactions/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert - Verify the transaction is persisted
        Optional<TaxTransaction> savedTransaction = taxTransactionRepository.findByTransactionId(transactionId);
        assertTrue(savedTransaction.isPresent());
        assertEquals(customerId, savedTransaction.get().getCustomerId());
        assertEquals(TransactionType.SALE, savedTransaction.get().getTransactionType());
        assertEquals(LocalDate.now(), savedTransaction.get().getDate());
    }

    @Test
    @DisplayName("Should handle batch request with invalid data")
    void testCreateBatchTransaction_InvalidData() throws Exception {
        // Arrange - Empty request body
        String requestBody = "[]";

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalTransactions", is(0)))
                .andReturn();
    }

    @Test
    @DisplayName("Should return correct validation status in response")
    void testGetTransactionById_ValidationStatus() throws Exception {
        // Arrange - Create transaction with failure status
        TaxTransaction transaction = new TaxTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setCustomerId(customerId);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setValidationStatus(ValidationStatus.FAILURE);
        transaction.setFailureReason("Tax rate mismatch");
        transaction.setDate(LocalDate.now());
        transaction.setCreatedAt(LocalDate.now());
        taxTransactionRepository.save(transaction);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationStatus", is(ValidationStatus.FAILURE.toString())))
                .andExpect(jsonPath("$.failureReason", is("Tax rate mismatch")))
                .andReturn();
    }

    @Test
    @DisplayName("Should handle multiple sequential get requests")
    void testMultipleGetRequests_Consistency() throws Exception {
        // Arrange
        TaxTransaction transaction = new TaxTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setCustomerId(customerId);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setValidationStatus(ValidationStatus.SUCCESS);
        transaction.setDate(LocalDate.now());
        transaction.setCreatedAt(LocalDate.now());
        taxTransactionRepository.save(transaction);

        // Act & Assert - Call the same endpoint multiple times
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/v1/transactions/{transactionId}", transactionId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactionId", is(transactionId.toString())))
                    .andReturn();
        }
    }

    @Test
    @DisplayName("Should accept different transaction types")
    void testCreateBatchTransaction_DifferentTransactionTypes() throws Exception {
        // Arrange
        List<TaxTransactionRequest> requests = Arrays.asList(
                new TaxTransactionRequest(UUID.randomUUID(), LocalDate.now(), customerId,
                        BigDecimal.valueOf(1000), BigDecimal.valueOf(0.18), BigDecimal.valueOf(180), TransactionType.SALE),
                new TaxTransactionRequest(UUID.randomUUID(), LocalDate.now(), customerId,
                        BigDecimal.valueOf(500), BigDecimal.valueOf(0.18), BigDecimal.valueOf(90), TransactionType.REFUND),
                new TaxTransactionRequest(UUID.randomUUID(), LocalDate.now(), customerId,
                        BigDecimal.valueOf(200), BigDecimal.valueOf(0.18), BigDecimal.valueOf(36), TransactionType.EXPENSE)
        );
        String requestBody = objectMapper.writeValueAsString(requests);

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalTransactions", is(3)))
                .andExpect(jsonPath("$.transactions", hasSize(3)))
                .andReturn();
    }

    @Test
    @DisplayName("Should return response with all required fields")
    void testGetAllTransactions_ResponseFields() throws Exception {
        // Arrange
        TaxTransaction transaction = new TaxTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setCustomerId(customerId);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTaxRate(BigDecimal.valueOf(0.18));
        transaction.setReportedTax(BigDecimal.valueOf(180));
        transaction.setValidationStatus(ValidationStatus.SUCCESS);
        transaction.setDate(LocalDate.now());
        transaction.setCreatedAt(LocalDate.now());
        taxTransactionRepository.save(transaction);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].transactionId", notNullValue()))
                .andExpect(jsonPath("$[0].customerId", notNullValue()))
                .andExpect(jsonPath("$[0].validationStatus", notNullValue()))
                .andExpect(jsonPath("$[0].createdDate", notNullValue()))
                .andReturn();
    }
}
