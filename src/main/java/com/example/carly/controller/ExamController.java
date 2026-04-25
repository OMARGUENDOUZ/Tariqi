package com.example.carly.controller;

import com.example.carly.dto.examstudent.ExamStudentCreateRequest;
import com.example.carly.dto.examstudent.ExamStudentFilter;
import com.example.carly.dto.examstudent.ExamStudentResponse;
import com.example.carly.dto.examstudent.ExamStudentUpdateRequest;
import com.example.carly.mapper.ExamStudentMapper;
import com.example.carly.model.ExamStatus;
import com.example.carly.model.ExamStudent;
import com.example.carly.service.ExamService;
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
@RequestMapping("/api/v1/exam-students")
public class ExamController {

    private final ExamService examService;
    private final ExamStudentMapper examStudentMapper;

    public ExamController(ExamService examService,
                          ExamStudentMapper examStudentMapper) {
        this.examService = examService;
        this.examStudentMapper = examStudentMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamStudentResponse> findById(@PathVariable long id) {
        return examService.findById(id)
                .map(examStudentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExamStudentResponse>> findAll(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) ExamStatus status,
            @RequestParam(required = false) Long examSlotId) {

        ExamStudentFilter filter = new ExamStudentFilter(studentId, status, examSlotId);
        List<ExamStudentResponse> result = examService.findWithFilter(filter)
                .stream()
                .map(examStudentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ExamStudentResponse>> findAllPaged(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(examService.findAll(pageable).map(examStudentMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<ExamStudentResponse> save(
            @RequestBody @Valid ExamStudentCreateRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
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
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamStudentResponse> update(
            @PathVariable long id,
            @RequestBody @Valid ExamStudentUpdateRequest body) {
        ExamStudent patch = new ExamStudent();
        patch.setResult(body.result());
        patch.setStatus(body.status());
        patch.setCategory(body.category());
        ExamStudent updated = examService.update(id, patch);
        return ResponseEntity.ok(examStudentMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        examService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
