package com.bank.dto;

import com.bank.enums.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequestDTO {

    @NotNull(message = "eventType is required")
    private EventType eventType;

    @NotNull(message = "transactionId is required")
    private UUID transactionId;

    @NotNull(message = "detailJson is required")
    private JsonNode detailJson;

    private String correlationId;

    private String serviceName;
}
