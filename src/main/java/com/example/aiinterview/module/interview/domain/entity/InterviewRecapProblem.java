package com.example.aiinterview.module.interview.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("interview_recap_problem")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewRecapProblem {
    @Id
    private Long id;

    @Column("user_answer")
    private String userAnswer;

    @Column("status")
    private String status;

    @Column("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @Column("irs_id")
    private Long interviewRecapSessionId; // interview_recap_session 테이블의 id 참조

    @Column("im_id")
    private Long interviewMessageId; // interview_recap_session 테이블의 id 참조

    // Getter, Setter, Constructor 등
}