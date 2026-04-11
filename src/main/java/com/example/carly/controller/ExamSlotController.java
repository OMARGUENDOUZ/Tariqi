package com.example.carly.controller;

import com.example.carly.dto.examslot.ExamSlotRequest;
import com.example.carly.dto.examslot.ExamSlotResponse;
import com.example.carly.mapper.ExamSlotMapper;
import com.example.carly.model.ExamSlot;
import com.example.carly.repository.ExamSlotRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/exam-slots")
public class ExamSlotController {

    private final ExamSlotRepository examSlotRepository;
    private final ExamSlotMapper examSlotMapper;

    public ExamSlotController(ExamSlotRepository examSlotRepository,
                              ExamSlotMapper examSlotMapper) {
        this.examSlotRepository = examSlotRepository;
        this.examSlotMapper = examSlotMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSlotResponse> findById(@PathVariable long id) {
        return examSlotRepository.findById(id)
                .map(examSlotMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExamSlotResponse>> findAll(
            @RequestParam(required = false) Boolean active) {

        List<ExamSlot> examSlots = Boolean.TRUE.equals(active)
                ? examSlotRepository.findByActive(true)
                : examSlotRepository.findAll();

        return ResponseEntity.ok(
                examSlots.stream()
                        .map(examSlotMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<ExamSlotResponse> save(
            @RequestBody @Valid ExamSlotRequest body,
            UriComponentsBuilder uriComponentsBuilder) {

        ExamSlot saved = examSlotRepository.save(examSlotMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/exam-slots/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(examSlotMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamSlotResponse> update(
            @PathVariable long id,
            @RequestBody @Valid ExamSlotRequest body) {

        Optional<ExamSlot> existing = examSlotRepository.findById(id);

        if (existing.isPresent()) {
            ExamSlot toUpdate = examSlotMapper.toEntity(body);
            toUpdate.setId(id);
            ExamSlot updated = examSlotRepository.save(toUpdate);
            return ResponseEntity.ok(examSlotMapper.toResponse(updated));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (examSlotRepository.existsById(id)) {
            examSlotRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}