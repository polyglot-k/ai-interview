package com.example.aiinterview.module.auth.domain;

import com.example.aiinterview.module.auth.exception.InvalidTokenException;

public record AccessToken(String value) {
    public AccessToken {
        if (value == null || value.isBlank()) {
            throw new InvalidTokenException();
        }
    }

    public static AccessToken of(String value) {
        return new AccessToken(value);
    }
}
