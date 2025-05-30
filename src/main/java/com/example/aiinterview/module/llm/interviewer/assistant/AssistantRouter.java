package com.example.aiinterview.module.llm.interviewer.assistant;

import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AssistantRouter {

    private final Map<LLMPromptType, ReactiveAssistant> routingMap;

    public ReactiveAssistant get(LLMPromptType type) {
        return routingMap.get(type);
    }
}