package com.example.aiinterview.module.llm.analysis.dto;

public record PartialEvaluation(
        Long messageId,
        double score,
        String coreQuestion,
        String feedback
){
}
