package com.example.aiinterview.module.interview.domain.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface InterviewMessageRepository extends ReactiveCrudRepository<InterviewMessage, Long>, InterviewMessageCompositeRepositoryCustom {
    //TODO: UPDATE 쿼리 쿼리 튜닝 대상
    @Modifying
    @Query("""
    UPDATE interview_message
    SET user_content = :userContent, user_created_at = :userCreatedAt
    WHERE i_id = :sessionId
    ORDER BY llm_created_at DESC
    LIMIT 1
            """)

    Mono<Void> saveUserMessage(Long sessionId, String userContent, LocalDateTime userCreatedAt);

    Mono<Long> countBySessionId(Long sessionId);

    Flux<InterviewMessage> findBySessionId(Long sessionId);

    @Query("""
     SELECT
        m.id ,
        m.user_content
    FROM
    interview_message m
    where m.id = :messageId;
    """)
    Mono<InterviewMessage> findUserContent(Long messageId);

    @Query("""
     SELECT
        m.id ,
        m.llm_content
    FROM
    interview_message m
    where m.id = :messageId;
    """)
    Mono<InterviewMessage> findLlmContent(Long messageId);
}
