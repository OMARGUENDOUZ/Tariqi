package com.example.carly.service;

import com.example.carly.model.Payment;
import com.example.carly.model.PaymentStatus;
import com.example.carly.model.Student;
import com.example.carly.repository.PaymentRepository;
import com.example.carly.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          StudentRepository studentRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    public Payment recordPayment(Long studentId, BigDecimal amount) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PAID);
        payment.setDate(new Date());
        return paymentRepository.save(payment);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(long id) {
        return paymentRepository.findById(id);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Optional<Payment> update(long id, Payment payment) {
        if (!paymentRepository.existsById(id)) {
            return Optional.empty();
        }
        payment.setId(id);
        return Optional.of(paymentRepository.save(payment));
    }

    public boolean deleteById(long id) {
        if (!paymentRepository.existsById(id)) {
            return false;
        }
        paymentRepository.deleteById(id);
        return true;
    }
}