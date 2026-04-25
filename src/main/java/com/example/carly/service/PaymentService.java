package com.example.carly.service;

import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.Payment;
import com.example.carly.model.PaymentStatus;
import com.example.carly.model.Student;
import com.example.carly.repository.PaymentRepository;
import com.example.carly.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
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

    public Page<Payment> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    public Optional<Payment> findById(long id) {
        return paymentRepository.findById(id);
    }

    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment update(long id, Payment patch) {
        Payment existing = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        existing.setStudent(patch.getStudent());
        existing.setStatus(patch.getStatus());
        existing.setAmount(patch.getAmount());
        existing.setDate(patch.getDate());
        return paymentRepository.save(existing);
    }

    public void deleteById(long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment", id);
        }
        paymentRepository.deleteById(id);
    }
}
