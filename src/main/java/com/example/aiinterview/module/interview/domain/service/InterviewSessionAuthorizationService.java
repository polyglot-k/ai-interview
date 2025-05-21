package com.example.aiinterview.module.interview.domain.service;

import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.entity.InterviewSessionStatus;
import com.example.aiinterview.module.interview.exception.InterviewAccessDeniedException;
import com.example.aiinterview.module.interview.exception.InterviewAlreadyEndedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InterviewSessionAuthorizationService {

    public Mono<Void> validateAccess(InterviewSession session, Long userId) {
        return assertUserIsOwner(session, userId)
                .then(assertSessionNotEnded(session));
    }

    public Mono<Void> assertUserIsOwner(InterviewSession session, Long userId) {
        if (!session.getIntervieweeId().equals(userId)) {
            return Mono.error(new InterviewAccessDeniedException());
        }
        return Mono.empty();
    }

    public Mono<Void> assertSessionNotEnded(InterviewSession session) {
        if (InterviewSessionStatus.ENDED.equals(session.getStatus())) {
            return Mono.error(new InterviewAlreadyEndedException());
        }
        return Mono.empty();
    }
}
