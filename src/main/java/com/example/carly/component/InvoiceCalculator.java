package com.example.carly.component;

import com.example.carly.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class InvoiceCalculator {

    public InvoiceCalculationResult calculate(Student student, Pricing pricing, List<ExamStudent> exams) {
        BigDecimal totalExamFees = BigDecimal.ZERO;
        List<Map<String, Object>> items = new ArrayList<>();

        // Base Course Fee
        items.add(Map.of(
                "description", "Frais de formation (" + student.getRequestedLicense() + ")",
                "amount", pricing.getBaseCourseFee()));

        // Exam Fees
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
                // Standard billing for PENDING/PASS/FAIL
                examTotal = examTotal.add(pricing.getExamUnitFee());
                examTotal = examTotal.add(pricing.getStampUnitFee());
                desc += " + Timbre";
            }

            if (examTotal.compareTo(BigDecimal.ZERO) > 0) {
                totalExamFees = totalExamFees.add(examTotal);
                items.add(Map.of(
                        "description", desc,
                        "amount", examTotal));
            }
        }

        BigDecimal totalAmount = pricing.getBaseCourseFee().add(totalExamFees);
        String breakdownJson = serializeBreakdown(items);

        return new InvoiceCalculationResult(totalAmount, breakdownJson);
    }

    private String serializeBreakdown(List<Map<String, Object>> items) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(items);
        } catch (Exception e) {
            return "[]";
        }
    }

    // Inner DTO for result
    public record InvoiceCalculationResult(BigDecimal totalAmount, String breakdown) {
    }
}
