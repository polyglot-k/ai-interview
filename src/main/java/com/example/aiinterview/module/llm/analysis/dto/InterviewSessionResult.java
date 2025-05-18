package com.example.aiinterview.module.llm.analysis.dto;

import com.example.aiinterview.module.interview.domain.entity.InterviewDetailFeedback;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewResultSummary;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
// overall -> total
@Slf4j
public record InterviewSessionResult(
        double overallAverageScore,
        String overallFeedback,
        List<PartialEvaluation> evaluations
) {}
