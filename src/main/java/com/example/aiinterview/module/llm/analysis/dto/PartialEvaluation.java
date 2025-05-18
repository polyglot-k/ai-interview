package com.example.aiinterview.module.llm.analysis.dto;

public record PartialEvaluation(
        Long llmMessageId,
        Long userMessageId,
        double score,
        String feedback
){
}
