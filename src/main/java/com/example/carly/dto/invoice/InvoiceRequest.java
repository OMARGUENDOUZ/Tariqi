package com.example.carly.dto.invoice;

import com.example.carly.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceRequest(
        @NotNull Long studentId,
        PaymentStatus status,
        BigDecimal baseCourseFee,
        BigDecimal examUnitFee,
        BigDecimal stampUnitFee,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String breakdown
) {
}