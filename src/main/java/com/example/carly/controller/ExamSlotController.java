package com.example.carly.controller;

import com.example.carly.model.ExamStatus;
import com.example.carly.model.ExamSlot;
import com.example.carly.repository.ExamSlotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("ExamSlot")
public class ExamSlotController {

    private final ExamSlotRepository examSlotRepository;

    public ExamSlotController(ExamSlotRepository examSlotRepository) {
        this.examSlotRepository = examSlotRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSlot> findById(@PathVariable long id) {
        Optional<ExamSlot> exam = examSlotRepository.findById(id);

        return exam.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExamSlot>> findAll(@RequestParam(required = false) Boolean active) {
        List<ExamSlot> examSlots;
        if (Boolean.TRUE.equals(active)) {
            // In a real scenario we might also filter by Date > now(), but for now active
            // flag is enough as per request intent "status active"
            examSlots = examSlotRepository.findByActive(true);
        } else {
            examSlots = examSlotRepository.findAll();
        }
        return !examSlots.isEmpty() ? ResponseEntity.ok(examSlots) : ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ExamSlot save(@RequestBody ExamSlot body, UriComponentsBuilder uriComponentsBuilder) {
        ExamSlot examSlot = examSlotRepository.save(body);
        URI location = uriComponentsBuilder.path("/Exam/{id}").buildAndExpand(examSlot).toUri();
        return ResponseEntity.created(location).body(examSlot).getBody();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamSlot> update(@PathVariable long id, @RequestBody ExamSlot body) {
        Optional<ExamSlot> exam = examSlotRepository.findById(id);

        if (exam.isPresent()) {
            ExamSlot updated = examSlotRepository.save(body);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ExamSlot> delete(@PathVariable long id) {
        if (examSlotRepository.existsById(id)) {
            examSlotRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
