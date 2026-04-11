package com.example.carly.controller;

import com.example.carly.dto.examstudent.ExamStudentCreateRequest;
import com.example.carly.dto.examstudent.ExamStudentResponse;
import com.example.carly.dto.examstudent.ExamStudentUpdateRequest;
import com.example.carly.mapper.ExamStudentMapper;
import com.example.carly.model.ExamStatus;
import com.example.carly.model.ExamStudent;
import com.example.carly.repository.ExamRepository;
import com.example.carly.service.ExamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/exam-students")
public class ExamController {

    private final ExamRepository examRepository;
    private final ExamService examService;
    private final ExamStudentMapper examStudentMapper;

    public ExamController(ExamRepository examRepository,
                          ExamService examService,
                          ExamStudentMapper examStudentMapper) {
        this.examRepository = examRepository;
        this.examService = examService;
        this.examStudentMapper = examStudentMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamStudentResponse> findById(@PathVariable long id) {
        return examRepository.findById(id)
                .map(examStudentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExamStudentResponse>> findAll(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) ExamStatus status,
            @RequestParam(required = false) Long examSlotId) {

        List<ExamStudent> examStudents;

        if (examSlotId != null) {
            examStudents = examRepository.findByExamSlotId(examSlotId);
        } else if (studentId == null && status == null) {
            examStudents = examRepository.findAll().stream().toList();
        } else if (studentId != null && status == null) {
            examStudents = examRepository.findByStudentId(studentId);
        } else if (studentId == null) {
            examStudents = examRepository.findByStatus(status);
        } else {
            examStudents = examRepository.findByStudentIdAndStatus(studentId, status);
        }

        return ResponseEntity.ok(
                examStudents.stream()
                        .map(examStudentMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<ExamStudentResponse> save(
            @RequestBody ExamStudentCreateRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        try {
            ExamStudent examStudent = examService.registerStudentForExam(
                    body.studentId(),
                    body.examSlotId(),
                    body.category()
            );
            URI location = uriComponentsBuilder
                    .path("/api/v1/exam-students/{id}")
                    .buildAndExpand(examStudent.getId())
                    .toUri();
            return ResponseEntity.created(location).body(examStudentMapper.toResponse(examStudent));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamStudentResponse> update(
            @PathVariable long id,
            @RequestBody ExamStudentUpdateRequest body) {
        Optional<ExamStudent> existingWrapper = examRepository.findById(id);

        if (existingWrapper.isPresent()) {
            ExamStudent existing = existingWrapper.get();
            examStudentMapper.updateEntity(existing, body);
            ExamStudent updated = examRepository.save(existing);
            return ResponseEntity.ok(examStudentMapper.toResponse(updated));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}