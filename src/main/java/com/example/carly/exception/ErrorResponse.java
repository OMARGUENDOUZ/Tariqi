package com.example.carly.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        Map<String, String> details
) {
    public Instant timestamp() {
        return Instant.now();
    }
}