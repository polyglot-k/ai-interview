package com.example.aiinterview.module.auth.domain;

import com.example.aiinterview.module.auth.exception.InvalidTokenException;

public record RefreshToken(String value) {
    public RefreshToken {
        if (value == null || value.isBlank()) {
            throw new InvalidTokenException();
        }
    }

    public static RefreshToken of(String value) {
        return new RefreshToken(value);
    }
}
