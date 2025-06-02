package com.example.aiinterview.module.interview.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InterviewAnalysisPayload {
    private Long sessionId;
    public static InterviewAnalysisPayload of(Long sessionId){
        return new InterviewAnalysisPayload(sessionId);
    }
}
