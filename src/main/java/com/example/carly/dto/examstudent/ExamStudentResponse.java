package com.example.carly.dto.examstudent;

import com.example.carly.model.ExamCategory;
import com.example.carly.model.ExamResult;
import com.example.carly.model.ExamStatus;

public record ExamStudentResponse(
        Long id,
        Long studentId,
        Long examSlotId,
        ExamCategory category,
        ExamStatus status,
        ExamResult result
) {
}