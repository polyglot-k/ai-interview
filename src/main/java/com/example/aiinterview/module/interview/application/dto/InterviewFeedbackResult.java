package com.example.aiinterview.module.interview.application.dto;

import com.example.aiinterview.module.interview.domain.entity.InterviewDetailFeedback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewFeedbackResult {
    private Long id;
    private String feedback;
    public static InterviewFeedbackResult of(InterviewDetailFeedback detailFeedback){
        return new InterviewFeedbackResult(detailFeedback.getId(), detailFeedback.getFeedback());
    }
}
