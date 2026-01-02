package com.example.carly.repository;

import com.example.carly.model.ExamSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Long> {
    List<ExamSlot> findByActive(Boolean active);
}
