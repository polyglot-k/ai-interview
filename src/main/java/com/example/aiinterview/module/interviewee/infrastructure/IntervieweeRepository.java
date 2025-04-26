package com.example.aiinterview.module.interviewee.infrastructure;

import com.example.aiinterview.module.interviewee.domain.entity.Interviewee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IntervieweeRepository extends ReactiveCrudRepository<Interviewee, Long> {
    Mono<Interviewee> findByEmail(String email);
}
