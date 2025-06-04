package com.example.aiinterview.module.interview.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@ToString
public class InterviewDetailFeedbackWithMessageId {
    private Long interviewFeedbackId;
    private Long interviewMessageId;
    private String coreQuestion;
    private Integer score;
    private LocalDateTime createdAt;
}
