package com.example.aiinterview.module.interview.domain.entity;

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
    private Integer score;

    @Column("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @Column("im_id")
    private Long interviewMessageId;
}
