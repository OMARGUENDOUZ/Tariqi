package com.example.carly.dto.examslot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExamSlotRequest(
        LocalDate examDate,
        String wilaya,
        LocalDateTime deadlineList,
        String center,
        Boolean active
) {
}
