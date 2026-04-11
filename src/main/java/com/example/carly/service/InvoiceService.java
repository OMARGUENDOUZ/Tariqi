package com.example.carly.service;

import com.example.carly.model.Invoice;
import com.example.carly.repository.InvoiceRepository;
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

    public Optional<Invoice> findById(long id) {
        return invoiceRepository.findById(id);
    }

    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> update(long id, Invoice invoice) {
        if (!invoiceRepository.existsById(id)) {
            return Optional.empty();
        }
        invoice.setId(id);
        return Optional.of(invoiceRepository.save(invoice));
    }

    public boolean deleteById(long id) {
        if (!invoiceRepository.existsById(id)) {
            return false;
        }
        invoiceRepository.deleteById(id);
        return true;
    }
}