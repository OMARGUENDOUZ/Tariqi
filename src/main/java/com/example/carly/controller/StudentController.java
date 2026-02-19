package com.example.carly.controller;

import com.example.carly.dto.StudentFilterDto;
import com.example.carly.model.Student;
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

    @Autowired
    private StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(@PathVariable long id) {
        Optional<Student> student = studentService.findById(id);

        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Student>> searchStudents(StudentFilterDto filters) {
        List<Student> students = studentService.findWithFilters(filters);
        return students.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(students);
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Student> uploadPhotoBase64(
            @PathVariable long id,
            @RequestBody Map<String, String> payload) {
        Student student = studentService.uploadPhotoBase64(id, payload);
        return (student != null) ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Student save(@RequestBody Student body, UriComponentsBuilder uriComponentsBuilder) {
        Student student = studentService.save(body);
        URI location = uriComponentsBuilder.path("/Student/{id}").buildAndExpand(student.getId()).toUri();
        return ResponseEntity.created(location).body(student).getBody();
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> update(@PathVariable long id, @RequestBody Student body) {
        Student student = studentService.save(id, body);
        return (student != null) ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> delete(@PathVariable long id) {
        if (studentService.existsById(id)) {
            studentService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
