package com.example.carly.service;

import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.Invoice;
import com.example.carly.repository.InvoiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    public Optional<Invoice> findById(long id) {
        return invoiceRepository.findById(id);
    }

    public Invoice create(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice update(long id, Invoice patch) {
        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
        existing.setStudentId(patch.getStudentId());
        existing.setStatus(patch.getStatus());
        existing.setBaseCourseFee(patch.getBaseCourseFee());
        existing.setExamUnitFee(patch.getExamUnitFee());
        existing.setStampUnitFee(patch.getStampUnitFee());
        existing.setTotalAmount(patch.getTotalAmount());
        existing.setPaidAmount(patch.getPaidAmount());
        existing.setBreakdown(patch.getBreakdown());
        return invoiceRepository.save(existing);
    }

    public void deleteById(long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice", id);
        }
        invoiceRepository.deleteById(id);
    }
}
