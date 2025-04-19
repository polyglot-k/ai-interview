package com.example.aiinterview.module.member.application;

import com.example.aiinterview.module.member.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.member.domain.entity.Member;
import reactor.core.publisher.Mono;

public interface MemberService {
    Mono<Member> create(CreateMemberRequest request);

}
