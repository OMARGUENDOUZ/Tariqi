package com.example.carly.dto.examslot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExamSlotRequest(
        @NotNull LocalDate examDate,
        @NotBlank String wilaya,
        @NotNull LocalDateTime deadlineList,
        String center,
        Boolean active
) {
}
