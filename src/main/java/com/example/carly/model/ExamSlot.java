package com.example.carly.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class ExamSlot {
    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    LocalDate examDate;      // 2025-12-15

    @Column(nullable = false, length = 50)
    String wilaya;

    LocalDateTime deadlineList;

    String center;

    boolean active = true;
}

