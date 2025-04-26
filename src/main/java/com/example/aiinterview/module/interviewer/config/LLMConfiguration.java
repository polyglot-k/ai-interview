package com.example.aiinterview.module.interviewer.config;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfiguration {
    @Value("${llm.google.api-key}")
    private String API_KEY;
    @Value("${llm.google.model-name}")
    private String MODEL_NAME;

    @Bean
    public StreamingChatLanguageModel geminiModel() {
        return GoogleAiGeminiStreamingChatModel.builder()
                .apiKey(API_KEY)
                .modelName(MODEL_NAME)
                .build();
    }
}
