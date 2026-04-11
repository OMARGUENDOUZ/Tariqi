package com.example.carly.dto.auth;

public record AuthUserDto(
        Long id,
        String email,
        String name,
        String role
) {
}