package com.example.aiinterview.module.member.application;

import com.example.aiinterview.module.member.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.member.domain.entity.Member;
import com.example.aiinterview.module.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    @Override
    public Mono<Member> create(CreateMemberRequest request) {
        Member member = Member.create(
                request.email(),
                request.name(),
                request.password()
        );
        return memberRepository.save(member);
    }
}
