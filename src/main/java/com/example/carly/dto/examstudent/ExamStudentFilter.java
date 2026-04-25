package com.example.carly.dto.examstudent;

import com.example.carly.model.ExamStatus;

public record ExamStudentFilter(
        Long studentId,
        ExamStatus status,
        Long examSlotId
) {
}
