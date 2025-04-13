package com.example.aiinterview.llm.prompt;

public enum LLMPromptType {
    FRONTEND,
    BACKEND;

    public static LLMPromptType from(String key) {
        return LLMPromptType.valueOf(key.toUpperCase());
    }
}
