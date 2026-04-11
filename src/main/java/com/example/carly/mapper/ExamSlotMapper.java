package com.example.carly.mapper;

import com.example.carly.dto.examslot.ExamSlotRequest;
import com.example.carly.dto.examslot.ExamSlotResponse;
import com.example.carly.model.ExamSlot;
import org.springframework.stereotype.Component;

@Component
public class ExamSlotMapper {

    public ExamSlotResponse toResponse(ExamSlot examSlot) {
        return new ExamSlotResponse(
                examSlot.getId(),
                examSlot.getExamDate(),
                examSlot.getWilaya(),
                examSlot.getDeadlineList(),
                examSlot.getCenter(),
                examSlot.isActive()
        );
    }

    public ExamSlot toEntity(ExamSlotRequest request) {
        ExamSlot examSlot = new ExamSlot();
        examSlot.setExamDate(request.examDate());
        examSlot.setWilaya(request.wilaya());
        examSlot.setDeadlineList(request.deadlineList());
        examSlot.setCenter(request.center());

        if (request.active() != null) {
            examSlot.setActive(request.active());
        }

        return examSlot;
    }
}
