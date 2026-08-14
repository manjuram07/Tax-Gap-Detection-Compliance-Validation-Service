package com.bank.dto;

import java.util.UUID;

public record CustomerExceptionCount(
        UUID customerId,
        long exceptionCount
) {
}