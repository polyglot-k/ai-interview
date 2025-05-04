package com.example.aiinterview.module.llm.analysis.dto;

public record PartialEvaluation(
        Long interviewMessageId,
        double score,
        String feedback
){}
