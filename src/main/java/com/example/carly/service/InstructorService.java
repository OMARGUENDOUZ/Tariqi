package com.example.carly.service;

import com.example.carly.model.Instructor;
import com.example.carly.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    public Optional<Instructor> findById(long id) {
        return instructorRepository.findById(id);
    }

    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Optional<Instructor> update(long id, Instructor instructor) {
        if (!instructorRepository.existsById(id)) {
            return Optional.empty();
        }
        instructor.setId(id);
        return Optional.of(instructorRepository.save(instructor));
    }

    public boolean deleteById(long id) {
        if (!instructorRepository.existsById(id)) {
            return false;
        }
        instructorRepository.deleteById(id);
        return true;
    }
}