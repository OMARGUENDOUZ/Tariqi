package com.example.carly.dto.invoice;

import com.example.carly.model.PaymentStatus;
import java.math.BigDecimal;

public record InvoiceResponse(
        Long id,
        Long studentId,
        PaymentStatus status,
        BigDecimal baseCourseFee,
        BigDecimal examUnitFee,
        BigDecimal stampUnitFee,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String breakdown
) {
}