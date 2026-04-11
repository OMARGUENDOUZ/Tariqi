package com.example.carly.mapper;

import com.example.carly.dto.invoice.InvoiceRequest;
import com.example.carly.dto.invoice.InvoiceResponse;
import com.example.carly.model.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getStudentId(),
                invoice.getStatus(),
                invoice.getBaseCourseFee(),
                invoice.getExamUnitFee(),
                invoice.getStampUnitFee(),
                invoice.getTotalAmount(),
                invoice.getPaidAmount(),
                invoice.getBreakdown()
        );
    }

    public Invoice toEntity(InvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setStudentId(request.studentId());
        invoice.setStatus(request.status());
        invoice.setBaseCourseFee(request.baseCourseFee());
        invoice.setExamUnitFee(request.examUnitFee());
        invoice.setStampUnitFee(request.stampUnitFee());
        invoice.setTotalAmount(request.totalAmount());
        invoice.setPaidAmount(request.paidAmount());
        invoice.setBreakdown(request.breakdown());
        return invoice;
    }
}