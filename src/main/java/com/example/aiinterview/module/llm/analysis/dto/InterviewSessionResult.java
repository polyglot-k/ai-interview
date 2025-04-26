package com.example.aiinterview.module.llm.analysis.dto;

import java.util.List;

public record InterviewSessionResult(
        double averageScore,
        List<PartialEvaluation> evaluations,
        String totalFeedback
) {}
