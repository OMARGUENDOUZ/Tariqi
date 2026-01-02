package com.example.carly.controller;

import com.example.carly.dto.StudentFilterDto;
import com.example.carly.model.Student;
import com.example.carly.repository.StudentRepository;
import com.example.carly.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/Student")
public class StudentController {
    private final StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(@PathVariable long id) {
        Optional<Student> student = studentRepository.findById(id);

        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Student>> searchStudents(StudentFilterDto filters) {
        List<Student> students = studentService.findWithFilters(filters);
        return students.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(students);
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Student> uploadPhotoBase64(
            @PathVariable long id,
            @RequestBody Map<String, String> payload
    ) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();
        student.setPhotoBase64(payload.get("photoBase64"));
        studentRepository.save(student);

        return ResponseEntity.ok(student);
    }

    @PostMapping
    public Student save(@RequestBody Student body, UriComponentsBuilder uriComponentsBuilder) {
        Student student = studentRepository.save(body);
        URI location = uriComponentsBuilder.path("/Student/{id}").buildAndExpand(student.getId()).toUri();
        return ResponseEntity.created(location).body(student).getBody();
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> update(@PathVariable long id, @RequestBody Student body) {
        Optional<Student> student = studentRepository.findById(id);

        if (student.isPresent()) {
            Student updated = studentRepository.save(body);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> delete(@PathVariable long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
