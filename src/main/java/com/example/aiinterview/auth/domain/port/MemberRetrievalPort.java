package com.example.aiinterview.auth.domain.port;

import com.example.aiinterview.auth.domain.AuthMember;

import java.util.Optional;

public interface MemberRetrievalPort {
    Optional<AuthMember> findByEmail(String email);
}
