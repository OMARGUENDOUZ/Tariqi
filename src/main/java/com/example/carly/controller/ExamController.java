package com.example.carly.controller;

import com.example.carly.model.ExamCategory;
import com.example.carly.model.ExamResult;
import com.example.carly.model.ExamStudent;
import com.example.carly.model.ExamStatus;
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

    public ExamController(ExamRepository examRepository, ExamService examService) {
        this.examRepository = examRepository;
        this.examService = examService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamStudent> findById(@PathVariable long id) {
        Optional<ExamStudent> exam = examRepository.findById(id);

        return exam.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExamStudent>> findAll(@RequestParam(required = false) Long studentId,
            @RequestParam(required = false) ExamStatus status,
            @RequestParam(required = false) Long examSlotId) {
        List<ExamStudent> examStudents;

        if (examSlotId != null) {
            examStudents = examRepository.findByExamSlotId(examSlotId);
        } else if (studentId == null && status == null) {
            examStudents = examRepository.findAll().stream().toList();
        } else if (studentId != null && status == null) {
            examStudents = examRepository.findByStudentId(studentId);
        } else if (studentId == null && status != null) {
            examStudents = examRepository.findByStatus(status);
        } else {
            examStudents = examRepository.findByStudentIdAndStatus(studentId, status);
        }
        return !examStudents.isEmpty() ? ResponseEntity.ok(examStudents) : ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ResponseEntity<ExamStudent> save(@RequestBody ExamStudent body, UriComponentsBuilder uriComponentsBuilder) {
        try {
            ExamStudent examStudent = examService.registerStudentForExam(body.getStudentId(), body.getExamSlotId(),
                    body.getCategory());
            URI location = uriComponentsBuilder.path("/ExamStudent/{id}").buildAndExpand(examStudent.getId()).toUri();
            return ResponseEntity.created(location).body(examStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // Simplify error handling
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamStudent> update(@PathVariable long id, @RequestBody ExamStudent body) {
        Optional<ExamStudent> existingWrapper = examRepository.findById(id);

        if (existingWrapper.isPresent()) {
            ExamStudent existing = existingWrapper.get();
            if (body.getResult() != null)
                existing.setResult(body.getResult());
            if (body.getStatus() != null)
                existing.setStatus(body.getStatus());
            // Update other fields if necessary
            if (body.getCategory() != null)
                existing.setCategory(body.getCategory());

            ExamStudent updated = examRepository.save(existing);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ExamStudent> delete(@PathVariable long id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
