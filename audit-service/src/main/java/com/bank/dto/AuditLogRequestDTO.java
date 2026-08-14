package com.bank.dto;

import com.bank.enums.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
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
    private Map<String, Object> detailJson;

    private String correlationId;

    private String serviceName;
}
