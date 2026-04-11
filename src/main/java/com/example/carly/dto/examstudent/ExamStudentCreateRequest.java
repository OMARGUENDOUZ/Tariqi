package com.example.carly.dto.examstudent;

import com.example.carly.model.ExamCategory;
import jakarta.validation.constraints.NotNull;

public record ExamStudentCreateRequest(
        @NotNull Long studentId,
        @NotNull Long examSlotId,
        @NotNull ExamCategory category
) {
}
