package com.example.carly.controller;

import com.example.carly.dto.payment.PaymentRequest;
import com.example.carly.dto.payment.PaymentResponse;
import com.example.carly.mapper.PaymentMapper;
import com.example.carly.model.Payment;
import com.example.carly.repository.PaymentRepository;
import com.example.carly.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentRepository paymentRepository,
                             PaymentService paymentService,
                             PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
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
        return paymentRepository.findById(id)
                .map(paymentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll() {
        List<PaymentResponse> payments = paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> save(
            @RequestBody PaymentRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Payment payment = paymentRepository.save(paymentMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/payments/{id}")
                .buildAndExpand(payment.getId())
                .toUri();
        return ResponseEntity.created(location).body(paymentMapper.toResponse(payment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(
            @PathVariable long id,
            @RequestBody PaymentRequest body) {
        Optional<Payment> existing = paymentRepository.findById(id);
        if (existing.isPresent()) {
            Payment toUpdate = paymentMapper.toEntity(body);
            toUpdate.setId(id);
            return ResponseEntity.ok(paymentMapper.toResponse(paymentRepository.save(toUpdate)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}