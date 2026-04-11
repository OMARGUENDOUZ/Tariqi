package com.example.carly.dto.examstudent;

import com.example.carly.model.ExamCategory;
import com.example.carly.model.ExamResult;
import com.example.carly.model.ExamStatus;
import jakarta.validation.constraints.NotNull;

public record ExamStudentUpdateRequest(
        @NotNull ExamResult result,
        @NotNull ExamStatus status,
        @NotNull ExamCategory category
) {
}
