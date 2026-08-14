package com.bank.client;


import com.bank.dto.TaxCalculationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Slf4j
@Component
public class TaxCalculationClient {

    private final WebClient webClient;

    public TaxCalculationClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:6001/api/v1").build();
    }

    @CircuitBreaker(name = "tax-calculation-service", fallbackMethod = "getTaxCalculationFallback")
    public TaxCalculationResponse getTaxCalculation(UUID transactionId) {
        log.info("Calling Tax Calculation Service for transactionId: {}", transactionId);
        return webClient.get()
                .uri("/{transactionId}", transactionId)
                .retrieve()
                .bodyToMono(TaxCalculationResponse.class)
                .block();
    }

    public TaxCalculationResponse getTaxCalculationFallback(UUID transactionId, Throwable throwable) {
        log.error("Error calling Tax Calculation Service for transactionId: {}. Error: {}", transactionId, throwable.getMessage());
        return new TaxCalculationResponse(null, null, null, null);
    }
}
