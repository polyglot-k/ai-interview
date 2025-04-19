package com.example.aiinterview.module.member.infrastructure;

import com.example.aiinterview.module.member.domain.entity.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {
    Mono<Member> findByEmail(String email);
}
