package com.example.aiinterview.module.interview.domain.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewDetailFeedback;
import com.example.aiinterview.module.interview.domain.vo.InterviewDetailFeedbackOverview;
import com.example.aiinterview.module.interview.domain.vo.InterviewDetailFeedbackWithMessageId;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InterviewDetailFeedbackRepository  extends R2dbcRepository<InterviewDetailFeedback, Long> {
    @Query("""
        SELECT
            f.id AS feedback_id,
        	m.id as interview_message_id,
            f.core_question,
            f.score,
            f.created_at AS created_at
        FROM
            interview_message m
        INNER JOIN
            interview_detail_feedback f ON f.m_id = m.id
        where m.i_id = :sessionId
        limit 20;
    """)
    Flux<InterviewDetailFeedbackOverview> findFeedbackOverviews(@Param("sessionId") Long sessionId);
    @Query("""
        SELECT
            f.id AS id,
            f.feedback_text
        FROM
        interview_detail_feedback f
        where f.m_id = :messageId;
    """)
    Mono<InterviewDetailFeedback> findFeedbackResultByMessageId(@Param("messageId") Long messageId);
}
