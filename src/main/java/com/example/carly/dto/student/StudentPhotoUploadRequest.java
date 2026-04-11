package com.example.carly.dto.student;

import jakarta.validation.constraints.NotBlank;

public record StudentPhotoUploadRequest(
        @NotBlank String photoBase64
) {
}
