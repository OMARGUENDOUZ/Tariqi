package com.example.carly.service;

import com.example.carly.exception.ResourceNotFoundException;
import com.example.carly.model.ExamSlot;
import com.example.carly.repository.ExamSlotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<ExamSlot> findAll(Pageable pageable) {
        return examSlotRepository.findAll(pageable);
    }

    public List<ExamSlot> findAllActive() {
        return examSlotRepository.findByActive(true);
    }

    public Optional<ExamSlot> findById(long id) {
        return examSlotRepository.findById(id);
    }

    public ExamSlot create(ExamSlot examSlot) {
        return examSlotRepository.save(examSlot);
    }

    public ExamSlot update(long id, ExamSlot patch) {
        ExamSlot existing = examSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExamSlot", id));
        existing.setExamDate(patch.getExamDate());
        existing.setWilaya(patch.getWilaya());
        existing.setDeadlineList(patch.getDeadlineList());
        existing.setCenter(patch.getCenter());
        existing.setActive(patch.isActive());
        return examSlotRepository.save(existing);
    }

    public void deleteById(long id) {
        if (!examSlotRepository.existsById(id)) {
            throw new ResourceNotFoundException("ExamSlot", id);
        }
        examSlotRepository.deleteById(id);
    }
}
