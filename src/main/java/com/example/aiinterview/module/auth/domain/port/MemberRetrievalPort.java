package com.example.aiinterview.module.auth.domain.port;

import com.example.aiinterview.module.member.domain.entity.Member;
import reactor.core.publisher.Mono;

public interface MemberRetrievalPort {
    Mono<Member> findByEmail(String email);
}
