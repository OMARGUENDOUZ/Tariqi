package com.example.carly.controller;

import com.example.carly.dto.StudentFilterDto;
import com.example.carly.dto.student.StudentPhotoUploadRequest;
import com.example.carly.dto.student.StudentRequest;
import com.example.carly.dto.student.StudentResponse;
import com.example.carly.mapper.StudentMapper;
import com.example.carly.model.Student;
import com.example.carly.service.StudentService;
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
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public StudentController(StudentService studentService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> findById(@PathVariable long id) {
        return studentService.findById(id)
                .map(studentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> searchStudents(StudentFilterDto filters) {
        List<Student> students = studentService.findWithFilters(filters);
        return ResponseEntity.ok(students.stream().map(studentMapper::toResponse).toList());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<StudentResponse>> findAllPaged(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(studentService.findAll(pageable).map(studentMapper::toResponse));
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<StudentResponse> uploadPhotoBase64(
            @PathVariable long id,
            @RequestBody @Valid StudentPhotoUploadRequest payload) {
        Student student = studentService.uploadPhotoBase64(id, payload.photoBase64());
        return ResponseEntity.ok(studentMapper.toResponse(student));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> save(
            @RequestBody @Valid StudentRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Student student = studentService.create(studentMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/students/{id}")
                .buildAndExpand(student.getId())
                .toUri();
        return ResponseEntity.created(location).body(studentMapper.toResponse(student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(
            @PathVariable long id,
            @RequestBody @Valid StudentRequest body) {
        Student updated = studentService.update(id, studentMapper.toEntity(body));
        return ResponseEntity.ok(studentMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
