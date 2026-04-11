package com.example.carly.dto.examslot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExamSlotResponse(
        Long id,
        LocalDate examDate,
        String wilaya,
        LocalDateTime deadlineList,
        String center,
        boolean active
) {
}