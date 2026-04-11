package com.example.carly.dto.examstudent;

import com.example.carly.model.ExamCategory;
import com.example.carly.model.ExamResult;
import com.example.carly.model.ExamStatus;

public record ExamStudentUpdateRequest(
        ExamResult result,
        ExamStatus status,
        ExamCategory category
) {
}
