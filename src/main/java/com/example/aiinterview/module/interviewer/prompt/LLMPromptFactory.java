package com.example.aiinterview.module.interviewer.prompt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class LLMPromptFactory {

    private final Map<LLMPromptType, String> prompts;

    public LLMPromptFactory(LLMPromptLoader promptLoader) {
        this.prompts = promptLoader.loadPrompts();
    }

    public String getPrompt(LLMPromptType type) {
        return switch (type){
            case FRONTEND,BACKEND -> prompts.get(type);
        };
    }

    public Set<LLMPromptType> availablePrompts() {
        return prompts.keySet();
    }
}