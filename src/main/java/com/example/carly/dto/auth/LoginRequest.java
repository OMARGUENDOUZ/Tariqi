package com.example.carly.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
