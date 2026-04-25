package com.example.carly.controller;

import com.example.carly.dto.instructor.InstructorRequest;
import com.example.carly.dto.instructor.InstructorResponse;
import com.example.carly.mapper.InstructorMapper;
import com.example.carly.model.Instructor;
import com.example.carly.service.InstructorService;
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
@RequestMapping("/api/v1/instructors")
public class InstructorController {

    private final InstructorService instructorService;
    private final InstructorMapper instructorMapper;

    public InstructorController(InstructorService instructorService,
                                InstructorMapper instructorMapper) {
        this.instructorService = instructorService;
        this.instructorMapper = instructorMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> findById(@PathVariable long id) {
        return instructorService.findById(id)
                .map(instructorMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InstructorResponse>> findAll() {
        return ResponseEntity.ok(
                instructorService.findAll().stream().map(instructorMapper::toResponse).toList()
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<InstructorResponse>> findAllPaged(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(instructorService.findAll(pageable).map(instructorMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<InstructorResponse> save(
            @RequestBody @Valid InstructorRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Instructor instructor = instructorService.create(instructorMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/instructors/{id}")
                .buildAndExpand(instructor.getId())
                .toUri();
        return ResponseEntity.created(location).body(instructorMapper.toResponse(instructor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponse> update(
            @PathVariable long id,
            @RequestBody @Valid InstructorRequest body) {
        Instructor updated = instructorService.update(id, instructorMapper.toEntity(body));
        return ResponseEntity.ok(instructorMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        instructorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
