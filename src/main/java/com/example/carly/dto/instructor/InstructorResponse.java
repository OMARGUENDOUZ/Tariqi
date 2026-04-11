package com.example.carly.dto.instructor;

import java.util.Date;

public record InstructorResponse(
        Long id,
        String firstName,
        String lastName,
        Date birthDate,
        String placeOfBirth,
        String address,
        String phoneNumber
) {
}
