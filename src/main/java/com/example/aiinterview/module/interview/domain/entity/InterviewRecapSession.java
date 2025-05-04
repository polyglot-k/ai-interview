package com.example.aiinterview.module.interview.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("interview_recap_session")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewRecapSession {
    @Id
    @Column("id")
    private Long id;
    @Column("started_at")
    private LocalDateTime startedAt;
    @Column("ended_at")
    private LocalDateTime endedAt;
    @Column("status")
    private String status;
    @Column("u_id")
    private Long userId; // users 테이블의 id 참조
    @Column("i_id")
    private Long interviewSessionId; // interview_session 테이블의 id 참조

    // Getter, Setter, Constructor 등
}