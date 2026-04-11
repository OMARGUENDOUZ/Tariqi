package com.example.carly.controller;

import com.example.carly.dto.invoice.InvoiceRequest;
import com.example.carly.dto.invoice.InvoiceResponse;
import com.example.carly.mapper.InvoiceMapper;
import com.example.carly.model.Invoice;
import com.example.carly.repository.InvoiceRepository;
import com.example.carly.service.FinanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final FinanceService financeService;
    private final InvoiceMapper invoiceMapper;

    public InvoiceController(InvoiceRepository invoiceRepository,
                             FinanceService financeService,
                             InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.financeService = financeService;
        this.invoiceMapper = invoiceMapper;
    }

    @PostMapping("/generate/{studentId}")
    public ResponseEntity<InvoiceResponse> generate(@PathVariable Long studentId) {
        Invoice invoice = financeService.generateInvoice(studentId);
        return ResponseEntity.ok(invoiceMapper.toResponse(invoice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> findById(@PathVariable long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> findAll() {
        List<InvoiceResponse> invoices = invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
        return ResponseEntity.ok(invoices);
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> save(
            @RequestBody @Valid InvoiceRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Invoice invoice = invoiceRepository.save(invoiceMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/invoices/{id}")
                .buildAndExpand(invoice.getId())
                .toUri();
        return ResponseEntity.created(location).body(invoiceMapper.toResponse(invoice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> update(
            @PathVariable long id,
            @RequestBody @Valid InvoiceRequest body) {
        Optional<Invoice> existing = invoiceRepository.findById(id);
        if (existing.isPresent()) {
            Invoice toUpdate = invoiceMapper.toEntity(body);
            toUpdate.setId(id);
            return ResponseEntity.ok(invoiceMapper.toResponse(invoiceRepository.save(toUpdate)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}