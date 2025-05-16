package com.example.aiinterview.module.llm.config;

import com.example.aiinterview.module.llm.interviewer.assistant.BackendAssistant;
import com.example.aiinterview.module.llm.interviewer.assistant.FrontendAssistant;
import com.example.aiinterview.module.llm.interviewer.assistant.ReactiveAssistant;
import com.example.aiinterview.module.llm.interviewer.memory.R2dbcChatMemoryStore;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AssistantConfiguration {
    private final R2dbcChatMemoryStore chatMemoryStore;
    @Bean
    public BackendAssistant backendAssistant(StreamingChatLanguageModel model){
        return AiServices.builder(BackendAssistant.class)
                .streamingChatLanguageModel(model)
                .chatMemoryProvider(memoryId -> memory())
                .build();
    }
    @Bean
    public FrontendAssistant frontendAssistant(StreamingChatLanguageModel model){
        return AiServices.builder(FrontendAssistant.class)
                .streamingChatLanguageModel(model)
                .chatMemoryProvider(memoryId -> memory())
                .build();
    }
    @Bean
    public Map<LLMPromptType, ReactiveAssistant> assistantMap(
            ReactiveAssistant backendAssistantAdapter,
            ReactiveAssistant frontendAssistantAdapter
    ) {
        return Map.of(
                    LLMPromptType.BACKEND, backendAssistantAdapter,
                    LLMPromptType.FRONTEND, frontendAssistantAdapter
                );
    }

    public ChatMemory memory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }
}