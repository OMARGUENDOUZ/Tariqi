package com.example.carly.dto.student;

import com.example.carly.model.*;

import java.util.Date;
import java.util.List;

public record StudentRequest(
        String photoBase64,
        String schoolId,
        String inscriptionId,
        StudentStatus status,
        String firstName,
        String lastName,
        Date birthDate,
        String placeOfBirth,
        String address,
        String phoneNumber,
        LicenseCategory requestedLicense,
        List<License> ownedLicense
) {
}
