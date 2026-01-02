package com.example.carly.dto;

import com.example.carly.model.*;
import lombok.Data;

import java.util.Date;

@Data
public class StudentFilterDto {

    private String firstName;
    private String lastName;
    private String fatherFirstName;
    private String fatherLastName;
    private String motherFirstName;
    private String motherLastName;
    private String phoneNumber;
    private GenderType gender;
    private LicenseCategory requestedLicense;
    private StudentStatus status;

    // Filtres sur exams
    private ExamStatus examStatus;
    private Date examDate;
    private ExamCategory examCategory;
    private ExamResult examResult;
}
