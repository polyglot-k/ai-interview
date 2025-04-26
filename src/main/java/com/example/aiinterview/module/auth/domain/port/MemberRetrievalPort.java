package com.example.aiinterview.module.auth.domain.port;

import com.example.aiinterview.module.interviewee.domain.entity.Interviewee;
import reactor.core.publisher.Mono;

public interface MemberRetrievalPort {
    Mono<Interviewee> findByEmail(String email);
}
