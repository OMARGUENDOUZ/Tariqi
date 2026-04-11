package com.example.carly.dto.instructor;

import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record InstructorRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        Date birthDate,
        String placeOfBirth,
        String address,
        @NotNull String phoneNumber
) {
}