package com.example.carly.controller;

import com.example.carly.model.Instructor;
import com.example.carly.repository.InstructorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {
    private final InstructorRepository instructorRepository;

    public InstructorController(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Instructor> findById(@PathVariable long id) {
        Optional<Instructor> instructor = instructorRepository.findById(id);

        return instructor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<Instructor> findAll() {
        Iterable<Instructor> students = instructorRepository.findAll();
        return ResponseEntity.ok(students).getBody();
    }

    @PostMapping
    public Instructor save(@RequestBody Instructor body, UriComponentsBuilder uriComponentsBuilder) {
        Instructor instructor = instructorRepository.save(body);
        URI location = uriComponentsBuilder.path("/Instructor/{id}").buildAndExpand(instructor.getId()).toUri();
        return ResponseEntity.created(location).body(instructor).getBody();
    }

    @PutMapping("{id}")
    public ResponseEntity<Instructor> update(@PathVariable long id, @RequestBody Instructor body) {
        Optional<Instructor> instructor = instructorRepository.findById(id);

        if(instructor.isPresent()) {
            Instructor updated = instructorRepository.save(body);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Instructor> delete(@PathVariable long id) {
        if(instructorRepository.existsById(id)) {
            instructorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
