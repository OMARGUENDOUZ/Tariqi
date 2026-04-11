package com.example.carly.dto.auth;

public record AuthResponse(
        String token,
        AuthUserDto user
) {
}