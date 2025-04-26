package com.example.aiinterview.module.interviewee.presentation;

import com.example.aiinterview.module.interviewee.application.IntervieweeService;
import com.example.aiinterview.module.interviewee.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.interviewee.domain.entity.Interviewee;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/interviewee")
@RequiredArgsConstructor
public class IntervieweeController {
    private final IntervieweeService intervieweeService;
    @GetMapping()
    public Mono<Void> test(@RequestAttribute("userId") Long userId){
        System.out.println(userId);
        return Mono.empty();
    }

    @PostMapping()
    public Mono<Interviewee> create(@RequestBody CreateMemberRequest request){
        return intervieweeService.create(request);
    }
}
