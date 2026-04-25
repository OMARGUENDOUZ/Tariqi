package com.example.carly.controller;

import com.example.carly.dto.payment.PaymentRequest;
import com.example.carly.dto.payment.PaymentResponse;
import com.example.carly.mapper.PaymentMapper;
import com.example.carly.model.Payment;
import com.example.carly.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService,
                             PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/record")
    public ResponseEntity<PaymentResponse> recordPayment(
            @RequestParam Long studentId,
            @RequestParam BigDecimal amount) {
        Payment payment = paymentService.recordPayment(studentId, amount);
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable long id) {
        return paymentService.findById(id)
                .map(paymentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll() {
        return ResponseEntity.ok(
                paymentService.findAll().stream().map(paymentMapper::toResponse).toList()
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PaymentResponse>> findAllPaged(
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(paymentService.findAll(pageable).map(paymentMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> save(
            @RequestBody @Valid PaymentRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Payment payment = paymentService.create(paymentMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/payments/{id}")
                .buildAndExpand(payment.getId())
                .toUri();
        return ResponseEntity.created(location).body(paymentMapper.toResponse(payment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(
            @PathVariable long id,
            @RequestBody @Valid PaymentRequest body) {
        Payment updated = paymentService.update(id, paymentMapper.toEntity(body));
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        paymentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
