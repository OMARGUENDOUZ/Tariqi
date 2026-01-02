package com.example.carly.controller;

import com.example.carly.model.Payment;
import com.example.carly.repository.PaymentRepository;
import jakarta.persistence.Id;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/Payment")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final com.example.carly.service.FinanceService financeService;

    public PaymentController(PaymentRepository paymentRepository,
            com.example.carly.service.FinanceService financeService) {
        this.paymentRepository = paymentRepository;
        this.financeService = financeService;
    }

    @PostMapping("/record")
    public ResponseEntity<Payment> record(@RequestParam Long studentId, @RequestParam java.math.BigDecimal amount) {
        return ResponseEntity.ok(financeService.recordPayment(studentId, amount));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> findById(@PathVariable long id) {
        Optional<Payment> payment = paymentRepository.findById(id);

        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<Payment> findAll() {
        Iterable<Payment> students = paymentRepository.findAll();
        return ResponseEntity.ok(students).getBody();
    }

    @PostMapping
    public Payment save(@RequestBody Payment body, UriComponentsBuilder uriComponentsBuilder) {
        Payment payment = paymentRepository.save(body);
        URI location = uriComponentsBuilder.path("/Payment/{id}").buildAndExpand(payment.getId()).toUri();
        return ResponseEntity.created(location).body(payment).getBody();
    }

    @PutMapping("{id}")
    public ResponseEntity<Payment> update(@PathVariable long id, @RequestBody Payment body) {
        Optional<Payment> payment = paymentRepository.findById(id);

        if (payment.isPresent()) {
            Payment updated = paymentRepository.save(body);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Payment> delete(@PathVariable long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
