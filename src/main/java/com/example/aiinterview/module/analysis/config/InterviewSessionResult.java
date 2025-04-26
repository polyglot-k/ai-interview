package com.example.aiinterview.module.analysis.config;

import java.util.List;

public record InterviewSessionResult(
        double averageScore,
        List<PartialEvaluation> evaluations,
        String totalFeedback
) {}
