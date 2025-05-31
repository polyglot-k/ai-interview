package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.exception.InterviewSessionNotFoundException;
import com.example.aiinterview.module.interview.domain.repository.InterviewSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {
    private final InterviewSessionRepository sessionRepository;

    public Mono<InterviewSession> create(Long memberId) {
        InterviewSession session = InterviewSession.create(memberId);
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<List<InterviewSession>> findByMemberId(Long memberId) {
        return sessionRepository.findByIntervieweeId(memberId)
                .collectList()
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<InterviewSession> findById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Mono<Void> complete(Long sessionId) {
        return findById(sessionId)
                .flatMap(session -> {
                    session.end();
                    return sessionRepository.save(session).then();
                });
    }
}
