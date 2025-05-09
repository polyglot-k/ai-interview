package com.example.aiinterview.module.llm.analysis;

import com.example.aiinterview.module.interview.infrastructure.repository.InterviewMessageRepository;
import com.example.aiinterview.module.llm.analysis.dto.InterviewSessionResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class InterviewSessionAnalyzer {

    private final ChatLanguageModel chatModel;
    private final InterviewMessageRepository repository;
    private final ResponseFormat responseFormat;
    private final ObjectMapper objectMapper;

    public Mono<InterviewSessionResult> analyze(Long interviewSessionId) {
        return loadInterviewSessionMessage(interviewSessionId)
                .flatMap(this::analyzeMessage);  // 전체를 하나로 묶는다
    }

    private Mono<InterviewSessionResult> analyzeMessage(String message) {
        return Mono.fromCallable(() -> {
                    log.info(message);
                    UserMessage userMessage = UserMessage.from(message);
                    ChatRequest chatRequest = createChatRequest(userMessage);
                    ChatResponse chatResponse = chatModel.chat(chatRequest);
                    String output = chatResponse.aiMessage().text();
                    return deserializeResult(output);
                })
                .subscribeOn(Schedulers.boundedElastic()); // CPU or Blocking 작업 분리
    }

    private Mono<String> loadInterviewSessionMessage(Long interviewSessionId) {
        return repository.findBySessionId(interviewSessionId)
                .map(msg ->"[ id : "+ msg.getId()+ "]" + msg.getSender() + ": " + msg.getMessage())
                .reduce((a, b) -> a + "\n" + b);
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
