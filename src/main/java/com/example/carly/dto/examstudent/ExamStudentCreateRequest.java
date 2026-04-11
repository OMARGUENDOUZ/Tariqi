package com.example.carly.dto.examstudent;

import com.example.carly.model.ExamCategory;

public record ExamStudentCreateRequest(
        Long studentId,
        Long examSlotId,
        ExamCategory category
) {
}
