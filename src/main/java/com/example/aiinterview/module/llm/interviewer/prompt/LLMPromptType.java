package com.example.aiinterview.module.llm.interviewer.prompt;

public enum LLMPromptType {
    FRONTEND,
    BACKEND;

    public static LLMPromptType from(String key) {
        return LLMPromptType.valueOf(key.toUpperCase());
    }
}
