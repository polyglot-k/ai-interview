package com.example.aiinterview.module.auth.domain.port;

import com.example.aiinterview.module.user.domain.entity.User;
import reactor.core.publisher.Mono;

public interface MemberRetrievalPort {
    Mono<User> findByEmail(String email);
}
