package com.example.carly.controller;

import com.example.carly.dto.instructor.InstructorRequest;
import com.example.carly.dto.instructor.InstructorResponse;
import com.example.carly.mapper.InstructorMapper;
import com.example.carly.model.Instructor;
import com.example.carly.repository.InstructorRepository;
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

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;

    public InstructorController(InstructorRepository instructorRepository,
                                InstructorMapper instructorMapper) {
        this.instructorRepository = instructorRepository;
        this.instructorMapper = instructorMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> findById(@PathVariable long id) {
        return instructorRepository.findById(id)
                .map(instructorMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InstructorResponse>> findAll() {
        List<InstructorResponse> instructors = instructorRepository.findAll()
                .stream()
                .map(instructorMapper::toResponse)
                .toList();
        return ResponseEntity.ok(instructors);
    }

    @PostMapping
    public ResponseEntity<InstructorResponse> save(
            @RequestBody InstructorRequest body,
            UriComponentsBuilder uriComponentsBuilder) {
        Instructor instructor = instructorRepository.save(instructorMapper.toEntity(body));
        URI location = uriComponentsBuilder
                .path("/api/v1/instructors/{id}")
                .buildAndExpand(instructor.getId())
                .toUri();
        return ResponseEntity.created(location).body(instructorMapper.toResponse(instructor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponse> update(
            @PathVariable long id,
            @RequestBody InstructorRequest body) {
        Optional<Instructor> existing = instructorRepository.findById(id);

        if (existing.isPresent()) {
            Instructor toUpdate = instructorMapper.toEntity(body);
            toUpdate.setId(id); // ✅ force l'ID pour éviter une création accidentelle
            Instructor updated = instructorRepository.save(toUpdate);
            return ResponseEntity.ok(instructorMapper.toResponse(updated));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (instructorRepository.existsById(id)) {
            instructorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}