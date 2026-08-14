package com.bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Violation {
    private String ruleCode;
    private String message;
    private String severity;
//    private OffsetDateTime detectedAt;
//    private Map<String, String> metadata;
//    private String suggestedRemediation;
}
