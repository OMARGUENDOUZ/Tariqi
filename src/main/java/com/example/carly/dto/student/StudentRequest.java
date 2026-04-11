package com.example.carly.dto.student;

import com.example.carly.model.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

public record StudentRequest(
        String photoBase64,
        @NotBlank String schoolId,
        @NotBlank String inscriptionId,
        StudentStatus status,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank Date birthDate,
        @NotBlank String placeOfBirth,
        @NotBlank String address,
        @NotBlank String phoneNumber,
        @NotBlank LicenseCategory requestedLicense,
        List<License> ownedLicense
) {
}
