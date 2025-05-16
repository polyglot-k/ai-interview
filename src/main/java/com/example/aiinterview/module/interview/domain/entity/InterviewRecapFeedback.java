package com.example.aiinterview.module.interview.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "interview_recap_feedback")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewRecapFeedback {
    @Id
    @Column("id")
    private Long id;

    @Column("recap_feedback")
    private String feedback;

    @Column("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @Column("irp_id") //FK
    private Long interviewRecapProblemId;

}
