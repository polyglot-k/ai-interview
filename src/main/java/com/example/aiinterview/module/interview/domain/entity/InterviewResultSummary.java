package com.example.aiinterview.module.interview.domain.entity;

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
    private Integer overallScore;

    @Column("overall_feedback")
    private String overallFeedback;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("i_id") // FK
    private Long interviewSessionId;
}
