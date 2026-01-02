package com.example.carly.service;

import com.example.carly.model.*;
import com.example.carly.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FinanceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final PricingRepository pricingRepository;

    public FinanceService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository,
            StudentRepository studentRepository, ExamRepository examRepository,
            PricingRepository pricingRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
        this.pricingRepository = pricingRepository;
    }

    @Transactional
    public Invoice generateInvoice(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Pricing pricing = pricingRepository.findByLicenseCategoryAndActiveTrue(student.getRequestedLicense())
                .orElseThrow(() -> new RuntimeException(
                        "No active pricing found for category: " + student.getRequestedLicense()));

        List<ExamStudent> exams = examRepository.findByStudentId(studentId);

        BigDecimal totalExamFees = BigDecimal.ZERO;
        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();

        // Add Base Course Fee Item
        items.add(java.util.Map.of(
                "description", "Frais de formation (" + student.getRequestedLicense() + ")",
                "amount", pricing.getBaseCourseFee()));

        for (ExamStudent exam : exams) {
            BigDecimal examTotal = BigDecimal.ZERO;
            String desc = "Examen " + exam.getCategory() + " (" + exam.getDate() + ")";

            if (exam.getResult() == ExamResult.ABSENT_UNJUSTIFIED) {
                if (pricing.isBillExamOnUnjustifiedAbsence()) {
                    examTotal = examTotal.add(pricing.getExamUnitFee());
                    desc += " [Exam Facturé]";
                }
                if (pricing.isBillStampOnUnjustifiedAbsence()) {
                    examTotal = examTotal.add(pricing.getStampUnitFee());
                    desc += " + Timbre (Absence NJ)";
                }
            } else if (exam.getResult() == ExamResult.ABSENT_JUSTIFIED) {
                if (pricing.isBillExamOnJustifiedAbsence()) {
                    examTotal = examTotal.add(pricing.getExamUnitFee());
                    desc += " [Exam Facturé]";
                }
                if (pricing.isBillStampOnJustifiedAbsence()) {
                    examTotal = examTotal.add(pricing.getStampUnitFee());
                    desc += " + Timbre (Absence J)";
                }
            } else {
                // PENDING / PASS / FAIL -> Always bill exam + stamp (Standard rule)
                // Unless we want PENDING to be free until passed? checking user request...
                // User said "30000 frais de formation et 2000 exam code... et 300 timbre"
                // Usually you pay for every attempt.
                examTotal = examTotal.add(pricing.getExamUnitFee());
                examTotal = examTotal.add(pricing.getStampUnitFee());
                desc += " + Timbre";
            }

            if (examTotal.compareTo(BigDecimal.ZERO) > 0) {
                totalExamFees = totalExamFees.add(examTotal);
                items.add(java.util.Map.of(
                        "description", desc,
                        "amount", examTotal));
            }
        }

        BigDecimal totalAmount = pricing.getBaseCourseFee().add(totalExamFees);

        // Calculate paid amount
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getStudent().getId() == studentId)
                .toList();

        BigDecimal paidAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Find existing invoice or create new
        Invoice invoice = invoiceRepository.findAll().stream()
                .filter(i -> i.getStudentId().equals(studentId))
                .findFirst()
                .orElse(new Invoice());

        invoice.setStudentId(studentId);
        invoice.setBaseCourseFee(pricing.getBaseCourseFee());
        invoice.setExamUnitFee(pricing.getExamUnitFee());
        invoice.setStampUnitFee(pricing.getStampUnitFee());
        invoice.setTotalAmount(totalAmount);
        invoice.setPaidAmount(paidAmount);

        // Serialize breakdown to JSON (simple string construction for minimal deps)
        try {
            ObjectMapper mapper = new ObjectMapper();
            invoice.setBreakdown(mapper.writeValueAsString(items));
        } catch (Exception e) {
            invoice.setBreakdown("[]");
        }

        invoice.setPaymentHistory(payments);

        if (paidAmount.compareTo(totalAmount) >= 0) {
            invoice.setStatus(PaymentStatus.PAID);
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(PaymentStatus.PARTIALLY_PAID);
        } else {
            invoice.setStatus(PaymentStatus.NOT_PAID);
        }

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Payment recordPayment(Long studentId, BigDecimal amount) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setAmount(amount);
        payment.setDate(new java.util.Date());
        payment.setStatus(PaymentStatus.PAID); // Individual payment success

        Payment saved = paymentRepository.save(payment);

        // Refresh Invoice
        generateInvoice(studentId);

        return saved;
    }
}
