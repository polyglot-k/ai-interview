package com.example.aiinterview.module.interview.domain.repository;

import com.example.aiinterview.module.interview.domain.vo.InterviewMessageWithStatus;
import reactor.core.publisher.Mono;

public interface InterviewMessageCompositeRepositoryCustom {
    Mono<InterviewMessageWithStatus> retrieveMessageWithStatusBySessionId(Long sessionId);
}
