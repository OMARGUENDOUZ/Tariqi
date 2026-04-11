package com.example.carly.mapper;

import com.example.carly.dto.examstudent.ExamStudentResponse;
import com.example.carly.dto.examstudent.ExamStudentUpdateRequest;
import com.example.carly.model.ExamStudent;
import org.springframework.stereotype.Component;

@Component
public class ExamStudentMapper {

    public ExamStudentResponse toResponse(ExamStudent examStudent) {
        return new ExamStudentResponse(
                examStudent.getId(),
                examStudent.getStudentId(),
                examStudent.getExamSlotId(),
                examStudent.getCategory(),
                examStudent.getStatus(),
                examStudent.getResult()
        );
    }

    public void updateEntity(ExamStudent examStudent, ExamStudentUpdateRequest request) {
        if (request.result() != null) {
            examStudent.setResult(request.result());
        }
        if (request.status() != null) {
            examStudent.setStatus(request.status());
        }
        if (request.category() != null) {
            examStudent.setCategory(request.category());
        }
    }
}
