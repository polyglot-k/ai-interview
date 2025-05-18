package com.example.aiinterview.module.interview.domain.entity;

import com.example.aiinterview.module.llm.analysis.dto.InterviewSessionResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewResultSummary {
    @Id
    @Column("id")
    private Long id;

    @Column("overall_score")
    private double overallScore;

    @Column("overall_feedback")
    private String overallFeedback;

    @Column("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @Column("i_id") // FK
    private Long interviewSessionId;

    public static InterviewResultSummary of(Long sessionId, String overallFeedback, double overallScore) {
        return InterviewResultSummary.builder()
                .interviewSessionId(sessionId)
                .overallScore(overallScore)
                .overallFeedback(overallFeedback)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
