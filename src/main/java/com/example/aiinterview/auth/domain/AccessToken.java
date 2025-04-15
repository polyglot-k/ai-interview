package com.example.aiinterview.auth.domain;

public record AccessToken(String value) {
    public AccessToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AccessToken cannot be null or blank");
        }
    }

    public static AccessToken of(String value) {
        return new AccessToken(value);
    }
}
