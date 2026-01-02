package com.example.carly.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Student {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Lob
        @Column(columnDefinition = "LONGTEXT")
        private String photoBase64;
        @Column(nullable = false)
        private String schoolId;
        private String inscriptionId;
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private StudentStatus status;
        @Embedded
        @Column(nullable = false)
        private HumanName name;
        @Column(nullable = false)
        private Date birthDate;
        private String placeOfBirth;
        private GenderType gender;
        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "firstName", column = @Column(name = "father_first_name")),
                        @AttributeOverride(name = "lastName", column = @Column(name = "father_last_name"))
        })
        private HumanName fatherName;
        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "firstName", column = @Column(name = "mother_first_name")),
                        @AttributeOverride(name = "lastName", column = @Column(name = "mother_last_name"))
        })
        private HumanName motherName;
        @Column(nullable = false)
        private String address;
        @Column(nullable = false)
        private String phoneNumber;
        @Column(nullable = false)
        private Date inscriptionSchoolDate;
        private Date inscriptionDate;
        @Column(nullable = false)
        private LicenseCategory requestedLicense;
        @ElementCollection
        @CollectionTable(name = "owned_licenses", joinColumns = @JoinColumn(name = "student_id"))
        private List<License> ownedLicense;

        @Transient
        private ExamCategory nextExam;

        @Transient
        private ExamStudent lastExam;
}
