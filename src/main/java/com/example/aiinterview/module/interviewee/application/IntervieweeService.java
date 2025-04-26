package com.example.aiinterview.module.interviewee.application;

import com.example.aiinterview.module.interviewee.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.interviewee.domain.entity.Interviewee;
import reactor.core.publisher.Mono;

public interface IntervieweeService {
    Mono<Interviewee> create(CreateMemberRequest request);

}
