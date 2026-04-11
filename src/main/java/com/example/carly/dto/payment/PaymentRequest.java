package com.example.carly.dto.payment;

import com.example.carly.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public record PaymentRequest(
        @NotNull Long studentId,
        @NotNull PaymentStatus status,
        @NotNull BigDecimal amount,
        Date date
) {
}