package com.example.aiinterview.llm.config;

import com.example.aiinterview.llm.assistant.*;
import com.example.aiinterview.llm.memory.PersistentChatMemoryStore;
import com.example.aiinterview.llm.prompt.LLMPromptType;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AssistantConfiguration {
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
    // Bean 으로 등록하면 안됨. memoryId 마다 memory 가 만들어져야하기 때문에 SingleTon 관리 X
    public ChatMemory memory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();
    }
}
