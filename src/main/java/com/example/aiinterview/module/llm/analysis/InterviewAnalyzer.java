package com.example.aiinterview.module.llm.analysis;

import com.example.aiinterview.module.llm.analysis.dto.InterviewSessionResult;
import com.example.aiinterview.module.llm.analysis.dto.PartialEvaluation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
@RequiredArgsConstructor
public class InterviewAnalyzer implements Analyzer<InterviewSessionResult>{

    private final ChatLanguageModel chatModel;
    private final ResponseFormat responseFormat;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<InterviewSessionResult> analyze(String message) {
        return Mono.fromCallable(() -> {
                    log.info(message);
                    UserMessage userMessage = UserMessage.from(message);
                    ChatRequest chatRequest = createChatRequest(userMessage);
                    ChatResponse chatResponse = chatModel.chat(chatRequest);
                    String output = chatResponse.aiMessage().text();
                    log.error("output : {}", output);
                    return deserializeResult(output);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ChatRequest createChatRequest(UserMessage userMessage) {
        return ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(userMessage)
                .build();
    }

    private InterviewSessionResult deserializeResult(String json) {
        try {
            return objectMapper.readValue(json, InterviewSessionResult.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}
