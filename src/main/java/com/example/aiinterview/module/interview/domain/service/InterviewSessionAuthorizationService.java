package com.example.aiinterview.module.interview.domain.service;

import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.entity.InterviewSessionStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InterviewSessionAuthorizationService {
    public Mono<Void> validateSessionAccess(InterviewSession session, Long memberId) {
        //streaming 이면 안되게
        if (!session.getIntervieweeId().equals(memberId)) {
            return Mono.error(new IllegalAccessException("접근 권한이 없습니다."));
        }
        if (InterviewSessionStatus.ENDED.equals(session.getStatus())) {
            return Mono.error(new IllegalArgumentException("이미 종료 된 면접 입니다."));
        }
        return Mono.empty();
    }
}
