package com.example.carly.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ExamStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JoinColumn(name = "student_id")
    private Long studentId;
    @JoinColumn(name = "exam_slot")
    private Long examSlotId;
    private ExamCategory category;
    private ExamStatus status;
    private ExamResult result;
    private Date date;
}
