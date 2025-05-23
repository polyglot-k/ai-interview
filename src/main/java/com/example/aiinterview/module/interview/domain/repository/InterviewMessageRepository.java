package com.example.aiinterview.module.interview.domain.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.vo.InterviewSender;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InterviewMessageRepository extends ReactiveCrudRepository<InterviewMessage, Long>,InterviewMessageCompositeRepositoryCustom{

    Mono<Long> countBySessionIdAndSender(Long sessionId, InterviewSender Sender);
    Flux<InterviewMessage> findBySessionId(Long sessionId);
}
