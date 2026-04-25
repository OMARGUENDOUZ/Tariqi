package com.example.carly.controller;

import com.example.carly.dto.examslot.ExamSlotRequest;
import com.example.carly.dto.examslot.ExamSlotResponse;
import com.example.carly.mapper.ExamSlotMapper;
import com.example.carly.model.ExamSlot;
import com.example.carly.service.ExamSlotService;
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
@RequestMapping("/api/v1/exam-slots")
public class ExamSlotController {

    private final ExamSlotService examSlotService;
    private final ExamSlotMapper examSlotMapper;

    public ExamSlotController(ExamSlotService examSlotService,
                              ExamSlotMapper examSlotMapper) {
        this.examSlotService = examSlotService;
        this.examSlotMapper = examSlotMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSlotResponse> findById(@PathVariable long id) {
        return examSlotService.findById(id)
                .map(examSlotMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExamSlotResponse>> findAll(
            @RequestParam(required = false) Boolean active) {
        List<ExamSlot> examSlots = Boolean.TRUE.equals(active)
                ? examSlotService.findAllActive()
                : examSlotService.findAll();
        return ResponseEntity.ok(
                examSlots.stream().map(examSlotMapper::toResponse).toList()
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ExamSlotResponse>> findAllPaged(
            @PageableDefault(size = 20, sort = "examDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(examSlotService.findAll(pageable).map(examSlotMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<ExamSlotResponse> save(
            @RequestBody @Valid ExamSlotRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        ExamSlot saved = examSlotService.create(examSlotMapper.toEntity(body));
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
        ExamSlot updated = examSlotService.update(id, examSlotMapper.toEntity(body));
        return ResponseEntity.ok(examSlotMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        examSlotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
