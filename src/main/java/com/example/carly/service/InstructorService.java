package com.example.carly.service;

import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.Instructor;
import com.example.carly.repository.InstructorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Instructor> findAll(Pageable pageable) {
        return instructorRepository.findAll(pageable);
    }

    public Optional<Instructor> findById(long id) {
        return instructorRepository.findById(id);
    }

    public Instructor create(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Instructor update(long id, Instructor patch) {
        Instructor existing = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", id));
        existing.setName(patch.getName());
        existing.setBirthDate(patch.getBirthDate());
        existing.setPlaceOfBirth(patch.getPlaceOfBirth());
        existing.setAddress(patch.getAddress());
        existing.setPhoneNumber(patch.getPhoneNumber());
        return instructorRepository.save(existing);
    }

    public void deleteById(long id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor", id);
        }
        instructorRepository.deleteById(id);
    }
}
