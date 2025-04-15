package com.example.aiinterview.auth.domain;

public record RefreshToken(String value) {
    public RefreshToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RefreshToken cannot be null or blank");
        }
    }

    public static RefreshToken of(String value) {
        return new RefreshToken(value);
    }
}
