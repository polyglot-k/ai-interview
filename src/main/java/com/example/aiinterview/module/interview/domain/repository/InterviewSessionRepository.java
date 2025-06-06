package com.example.aiinterview.module.interview.domain.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InterviewSessionRepository extends R2dbcRepository<InterviewSession, Long> {
    // 면접방 ID로 조회 (Mono는 비동기 결과를 나타냄)
    Mono<InterviewSession> findById(Long id);
    Flux<InterviewSession> findByIntervieweeId(Long intervieweeId);

    @Query("""
        SELECT 
        id,
        status, 
        u_id
        FROM interview_session force index(interview_session_covering_idx)
        where id=:sessionId;
    """)
    Mono<InterviewSession> findByIdForcedCoveringIndex(Long sessionId);
}

