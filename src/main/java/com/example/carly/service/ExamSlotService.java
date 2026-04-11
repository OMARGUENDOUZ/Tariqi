package com.example.carly.service;

import com.example.carly.model.ExamSlot;
import com.example.carly.repository.ExamSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamSlotService {

    private final ExamSlotRepository examSlotRepository;

    public ExamSlotService(ExamSlotRepository examSlotRepository) {
        this.examSlotRepository = examSlotRepository;
    }

    public List<ExamSlot> findAll() {
        return examSlotRepository.findAll();
    }

    public List<ExamSlot> findAllActive() {
        return examSlotRepository.findByActive(true);
    }

    public Optional<ExamSlot> findById(long id) {
        return examSlotRepository.findById(id);
    }

    public ExamSlot save(ExamSlot examSlot) {
        return examSlotRepository.save(examSlot);
    }

    public Optional<ExamSlot> update(long id, ExamSlot examSlot) {
        if (!examSlotRepository.existsById(id)) {
            return Optional.empty();
        }
        examSlot.setId(id);
        return Optional.of(examSlotRepository.save(examSlot));
    }

    public boolean deleteById(long id) {
        if (!examSlotRepository.existsById(id)) {
            return false;
        }
        examSlotRepository.deleteById(id);
        return true;
    }
}