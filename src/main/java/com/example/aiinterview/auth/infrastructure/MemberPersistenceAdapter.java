package com.example.aiinterview.auth.infrastructure;

import com.example.aiinterview.auth.domain.AuthMember;
import com.example.aiinterview.auth.domain.port.MemberRetrievalPort;
import com.example.aiinterview.member.domain.entity.Member;
import com.example.aiinterview.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRetrievalPort {
    private final MemberRepository memberRepository;
    @Override
    public Optional<AuthMember> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow();

        AuthMember authMember = AuthMember.of(
                member.getEmail(),
                member.getName()
        );
        return Optional.of(authMember);
    }
}
