package com.example.carly.controller;

import com.example.carly.dto.instructor.InstructorRequest;
import com.example.carly.dto.instructor.InstructorResponse;
import com.example.carly.mapper.InstructorMapper;
import com.example.carly.model.Instructor;
import com.example.carly.repository.InstructorRepository;
import com.example.carly.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {

    private final InstructorService instructorService;
    private final InstructorMapper instructorMapper;

    public InstructorController(InstructorService instructorRepository,
                                InstructorMapper instructorMapper) {
        this.instructorService = instructorRepository;
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
        List<InstructorResponse> instructors = instructorService.findAll()
                .stream()
                .map(instructorMapper::toResponse)
                .toList();
        return ResponseEntity.ok(instructors);
    }

    @PostMapping
    public ResponseEntity<InstructorResponse> save(
            @RequestBody @Valid InstructorRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Instructor instructor = instructorService.save(instructorMapper.toEntity(body));
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
        Optional<Instructor> existing = instructorService.findById(id);

        if (existing.isPresent()) {
            Instructor toUpdate = instructorMapper.toEntity(body);
            toUpdate.setId(id);
            Instructor updated = instructorService.save(toUpdate);
            return ResponseEntity.ok(instructorMapper.toResponse(updated));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (instructorService.findById(id).isPresent()) {
            instructorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}