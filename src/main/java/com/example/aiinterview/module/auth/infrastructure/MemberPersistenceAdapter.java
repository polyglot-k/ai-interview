package com.example.aiinterview.module.auth.infrastructure;

import com.example.aiinterview.module.auth.domain.port.MemberRetrievalPort;
import com.example.aiinterview.module.member.domain.entity.Member;
import com.example.aiinterview.module.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRetrievalPort {
    private final MemberRepository memberRepository;


    @Override
    public Mono<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Member not found")));
    }
}
