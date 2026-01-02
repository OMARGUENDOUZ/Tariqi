package com.example.carly.controller;

import com.example.carly.model.Invoice;
import com.example.carly.repository.InvoiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/Invoice")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final com.example.carly.service.FinanceService financeService;

    public InvoiceController(InvoiceRepository invoiceRepository,
            com.example.carly.service.FinanceService financeService) {
        this.invoiceRepository = invoiceRepository;
        this.financeService = financeService;
    }

    @PostMapping("/generate/{studentId}")
    public ResponseEntity<Invoice> generate(@PathVariable Long studentId) {
        return ResponseEntity.ok(financeService.generateInvoice(studentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> findById(@PathVariable long id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);

        return invoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<Invoice> findAll() {
        Iterable<Invoice> invoices = invoiceRepository.findAll();
        return ResponseEntity.ok(invoices).getBody();
    }

    @PostMapping
    public Invoice save(@RequestBody Invoice body, UriComponentsBuilder uriComponentsBuilder) {
        Invoice invoice = invoiceRepository.save(body);
        URI location = uriComponentsBuilder.path("/Invoice/{id}").buildAndExpand(invoice.getId()).toUri();
        return ResponseEntity.created(location).body(invoice).getBody();
    }

    @PutMapping("{id}")
    public ResponseEntity<Invoice> update(@PathVariable long id, @RequestBody Invoice body) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);

        if (invoice.isPresent()) {
            Invoice updated = invoiceRepository.save(body);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Invoice> delete(@PathVariable long id) {
        if (invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
