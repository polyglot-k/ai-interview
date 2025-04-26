package com.example.aiinterview.module.auth.infrastructure;

import com.example.aiinterview.module.auth.domain.port.MemberRetrievalPort;
import com.example.aiinterview.module.interviewee.domain.entity.Interviewee;
import com.example.aiinterview.module.interviewee.infrastructure.IntervieweeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRetrievalPort {
    private final IntervieweeRepository intervieweeRepository;


    @Override
    public Mono<Interviewee> findByEmail(String email) {
        return intervieweeRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("Interviewee not found")));
    }
}
