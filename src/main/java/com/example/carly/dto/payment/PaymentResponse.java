package com.example.carly.dto.payment;

import com.example.carly.model.PaymentStatus;
import java.math.BigDecimal;
import java.util.Date;

public record PaymentResponse(
        Long id,
        Long studentId,
        PaymentStatus status,
        BigDecimal amount,
        Date date
) {
}