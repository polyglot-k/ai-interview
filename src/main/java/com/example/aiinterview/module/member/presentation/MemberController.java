package com.example.aiinterview.module.member.presentation;

import com.example.aiinterview.module.member.application.MemberService;
import com.example.aiinterview.module.member.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    @GetMapping
    public Mono<Void> test(@RequestAttribute("userId") Long userId){
        System.out.println(userId);
        return Mono.empty();
    }
    public Mono<Member> create(@RequestBody CreateMemberRequest request){
        return memberService.create(request);
    }
}
