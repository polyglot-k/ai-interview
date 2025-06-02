package com.example.aiinterview.module.interview.domain.entity;

import com.example.aiinterview.module.llm.analysis.dto.PartialEvaluation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name="interview_detail_feedback")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class InterviewDetailFeedback {
    @Id
    @Column("id")
    private Long id;

    @Column("feedback_text")
    private String feedback;

    @Column("score")
    private double score;

    @Column("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @Column("m_id")
    private Long messageId;

    public static InterviewDetailFeedback of(PartialEvaluation evaluation) {
        return InterviewDetailFeedback.builder()
                .feedback(evaluation.feedback())
                .score(evaluation.score())
                .createdAt(LocalDateTime.now())
                .messageId(evaluation.messageId())
                .build();
    }
    public static InterviewDetailFeedback of(String feedback, double score, LocalDateTime createdAt, Long messageId) {
        return InterviewDetailFeedback.builder()
                .feedback(feedback)
                .score(score)
                .createdAt(createdAt)
                .messageId(messageId)
                .build();
    }
    public static InterviewDetailFeedback of(Long id, String feedback, double score, LocalDateTime createdAt, Long messageId) {
        return InterviewDetailFeedback.builder()
                .id(id)
                .feedback(feedback)
                .score(score)
                .createdAt(createdAt)
                .messageId(messageId)
                .build();
    }

}
