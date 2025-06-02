package com.example.aiinterview.module.llm.analysis.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
// overall -> total
@Slf4j
public record InterviewSessionResult(
        double overallAverageScore,
        String overallFeedback,
        List<PartialEvaluation> evaluations
) {}
