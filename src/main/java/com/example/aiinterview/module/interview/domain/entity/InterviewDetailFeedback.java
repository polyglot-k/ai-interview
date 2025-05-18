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

    @Column("llm_id")
    private Long llmMessageId;
    @Column("user_id")
    private Long userMessageId;

    public static InterviewDetailFeedback of(PartialEvaluation evaluation) {
        return InterviewDetailFeedback.builder()
                .feedback(evaluation.feedback())
                .score(evaluation.score())
                .createdAt(LocalDateTime.now())
                .llmMessageId(evaluation.llmMessageId())
                .userMessageId(evaluation.userMessageId())
                .build();
    }
}
