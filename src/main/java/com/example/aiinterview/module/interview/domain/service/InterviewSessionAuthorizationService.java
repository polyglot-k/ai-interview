package com.example.aiinterview.module.interview.domain.service;

import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.entity.InterviewSessionStatus;
import com.example.aiinterview.module.interview.exception.InterviewAccessDeniedException;
import com.example.aiinterview.module.interview.exception.InterviewAlreadyEndedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InterviewSessionAuthorizationService {
    public Mono<Void> validateSessionAccess(InterviewSession session, Long memberId) {
        //streaming 이면 안되게
        if (!session.getIntervieweeId().equals(memberId)) {
            return Mono.error(InterviewAccessDeniedException::new);
        }
        if (InterviewSessionStatus.ENDED.equals(session.getStatus())) {
            return Mono.error(InterviewAlreadyEndedException::new);
        }
        return Mono.empty();
    }
}
