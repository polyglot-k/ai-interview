package com.example.aiinterview.module.llm.analysis.dto;

public record PartialEvaluation(
        String problemContent,
        double score,
        String feedback
){}
