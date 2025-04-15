package com.example.aiinterview.auth.domain;

public record AuthMember(String email, String name) {

    public static AuthMember of(String email, String name) {
        return new AuthMember(email, name);
    }
}
