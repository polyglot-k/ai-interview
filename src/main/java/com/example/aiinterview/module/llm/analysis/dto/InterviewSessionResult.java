package com.example.aiinterview.module.llm.analysis.dto;

import java.util.List;
// overall -> total
public record InterviewSessionResult(
        double overallAverageScore,
        String overallFeedback,
        List<PartialEvaluation> evaluations
) {}
