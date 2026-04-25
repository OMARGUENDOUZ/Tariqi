package com.example.carly.controller;

import com.example.carly.dto.invoice.InvoiceRequest;
import com.example.carly.dto.invoice.InvoiceResponse;
import com.example.carly.mapper.InvoiceMapper;
import com.example.carly.model.Invoice;
import com.example.carly.service.FinanceService;
import com.example.carly.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final FinanceService financeService;
    private final InvoiceMapper invoiceMapper;

    public InvoiceController(InvoiceService invoiceService,
                             FinanceService financeService,
                             InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
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
        return invoiceService.findById(id)
                .map(invoiceMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> findAll() {
        return ResponseEntity.ok(
                invoiceService.findAll().stream().map(invoiceMapper::toResponse).toList()
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<InvoiceResponse>> findAllPaged(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(invoiceService.findAll(pageable).map(invoiceMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> save(
            @RequestBody @Valid InvoiceRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Invoice invoice = invoiceService.create(invoiceMapper.toEntity(body));
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
        Invoice updated = invoiceService.update(id, invoiceMapper.toEntity(body));
        return ResponseEntity.ok(invoiceMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        invoiceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
