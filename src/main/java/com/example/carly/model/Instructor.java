package com.example.carly.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    private HumanName name;
    private Date birthDate;
    private String placeOfBirth;
    private String address;
    private String phoneNumber;
}
