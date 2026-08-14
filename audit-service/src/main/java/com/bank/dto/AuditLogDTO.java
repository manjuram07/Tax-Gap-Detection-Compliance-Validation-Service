package com.bank.dto;

import com.bank.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {

    private UUID id;
    private EventType eventType;
    private UUID transactionId;
    private OffsetDateTime eventTimestamp;
    private Map<String, Object> detailJson;
    private String correlationId;
    private String serviceName;
    private OffsetDateTime createdAt;
}
