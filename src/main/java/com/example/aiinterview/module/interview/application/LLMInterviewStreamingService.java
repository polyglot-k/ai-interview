package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.infrastructure.MessageBufferRegistry;
import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import com.example.aiinterview.module.llm.interviewer.InterviewStreamer;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@Service
public class LLMInterviewStreamingService {
    private final InterviewStreamer interviewStreamer;
    private final InterviewMessageService messageService;
    private final MessageBufferRegistry bufferRegistry;

    public Flux<String> startInterviewStreaming(Long sessionId, LLMPromptType type ,String message) {
        return interviewStreamer.stream(sessionId.intValue(), type, message)
                .doOnNext(partialResponse -> appendPartialResponse(sessionId, partialResponse))
                .delayElements(java.time.Duration.ofMillis(50))
                .doOnComplete(()->saveResponse(sessionId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return bufferRegistry.retrieveMessageBuffer(sessionId);
    }
    private void appendPartialResponse(Long sessionId, String partialResponse) {
        bufferRegistry.appendMessageBuffer(sessionId, partialResponse)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(length -> log.debug("Room {}: Appended '{}', current length: {}", sessionId, partialResponse, length))
                .doOnError(error -> log.error("Room {}: Error appending '{}': {}", sessionId, partialResponse, error.getMessage()))
                .subscribe();
    }
    private void saveResponse(Long sessionId) {
        bufferRegistry.retrieveMessageBuffer(sessionId)
                .flatMap(fullResponse -> {
                    log.info("💬 스트림 완료 - 전체 응답 저장 시도: {}", fullResponse);
                    return messageService.saveMessage(sessionId, fullResponse, InterviewSender.LLM)
                            .then(bufferRegistry.clearMessageBuffer(sessionId));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
