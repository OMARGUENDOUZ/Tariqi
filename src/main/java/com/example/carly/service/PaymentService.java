package com.example.carly.service;

import com.example.carly.model.Payment;
import com.example.carly.model.PaymentStatus;
import com.example.carly.model.Student;
import com.example.carly.repository.PaymentRepository;
import com.example.carly.repository.StudentRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final FinanceService financeService;

    // Use @Lazy to avoid circular dependency if FinanceService also injects
    // PaymentService (unlikely but safe)
    public PaymentService(PaymentRepository paymentRepository,
            StudentRepository studentRepository,
            @Lazy FinanceService financeService) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.financeService = financeService;
    }

    @Transactional
    public Payment recordPayment(Long studentId, BigDecimal amount) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setAmount(amount);
        payment.setDate(new Date());
        payment.setStatus(PaymentStatus.PAID);

        Payment saved = paymentRepository.save(payment);

        // Trigger invoice update
        financeService.generateInvoice(studentId);

        return saved;
    }
}
