package com.example.carly.dto.payment;

import com.example.carly.model.PaymentStatus;
import java.math.BigDecimal;
import java.util.Date;

public record PaymentRequest(
        Long studentId,
        PaymentStatus status,
        BigDecimal amount,
        Date date
) {
}