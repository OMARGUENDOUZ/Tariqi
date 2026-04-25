package com.example.carly.service;

import com.example.carly.component.InvoiceCalculator;
import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.*;
import com.example.carly.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FinanceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final PricingRepository pricingRepository;
    private final InvoiceCalculator invoiceCalculator;

    public FinanceService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository,
            StudentRepository studentRepository, ExamRepository examRepository,
            PricingRepository pricingRepository, InvoiceCalculator invoiceCalculator) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
        this.pricingRepository = pricingRepository;
        this.invoiceCalculator = invoiceCalculator;
    }

    @Transactional
    public Invoice generateInvoice(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        Pricing pricing = pricingRepository.findByLicenseCategoryAndActiveTrue(student.getRequestedLicense())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active pricing found for category: " + student.getRequestedLicense()));

        List<ExamStudent> exams = examRepository.findByStudentId(studentId);

        InvoiceCalculator.InvoiceCalculationResult result = invoiceCalculator.calculate(student, pricing, exams);

        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getStudent().getId() == studentId)
                .toList();

        BigDecimal paidAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice invoice = invoiceRepository.findAll().stream()
                .filter(i -> i.getStudentId().equals(studentId))
                .findFirst()
                .orElse(new Invoice());

        invoice.setStudentId(studentId);
        invoice.setBaseCourseFee(pricing.getBaseCourseFee());
        invoice.setExamUnitFee(pricing.getExamUnitFee());
        invoice.setStampUnitFee(pricing.getStampUnitFee());
        invoice.setTotalAmount(result.totalAmount());
        invoice.setBreakdown(result.breakdown());
        invoice.setPaidAmount(paidAmount);
        invoice.setPaymentHistory(payments);

        if (paidAmount.compareTo(result.totalAmount()) >= 0) {
            invoice.setStatus(PaymentStatus.PAID);
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(PaymentStatus.PARTIALLY_PAID);
        } else {
            invoice.setStatus(PaymentStatus.NOT_PAID);
        }

        return invoiceRepository.save(invoice);
    }
}
