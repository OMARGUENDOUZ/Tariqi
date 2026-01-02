package com.example.carly.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(nullable = false, name = "student_id")
    private Long studentId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private BigDecimal baseCourseFee;
    private BigDecimal examUnitFee;
    private BigDecimal stampUnitFee;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String breakdown; // JSON string of line items

    @Transient
    private java.util.List<Payment> paymentHistory;
}
