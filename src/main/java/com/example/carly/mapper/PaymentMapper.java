package com.example.carly.mapper;

import com.example.carly.dto.payment.PaymentRequest;
import com.example.carly.dto.payment.PaymentResponse;
import com.example.carly.model.Payment;
import com.example.carly.model.Student;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        Long studentId = payment.getStudent() != null ? payment.getStudent().getId() : null;
        return new PaymentResponse(
                payment.getId(),
                studentId,
                payment.getStatus(),
                payment.getAmount(),
                payment.getDate()
        );
    }

    public Payment toEntity(PaymentRequest request) {
        Payment payment = new Payment();
        if (request.studentId() != null) {
            Student student = new Student();
            student.setId(request.studentId());
            payment.setStudent(student);
        }
        payment.setStatus(request.status());
        payment.setAmount(request.amount());
        payment.setDate(request.date());
        return payment;
    }
}