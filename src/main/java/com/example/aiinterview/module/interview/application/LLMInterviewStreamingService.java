package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import com.example.aiinterview.module.interview.exception.StreamingAlreadyInProgressException;
import com.example.aiinterview.module.interview.infrastructure.MessageBufferRegistry;
import com.example.aiinterview.module.interview.infrastructure.SessionStreamingStatusRegistry;
import com.example.aiinterview.module.llm.interviewer.InterviewStreamer;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;


@Slf4j
@RequiredArgsConstructor
@Service
public class LLMInterviewStreamingService {
    private final InterviewStreamer interviewStreamer;
    private final InterviewMessageService messageService;
    private final MessageBufferRegistry bufferRegistry;
    private final SessionStreamingStatusRegistry streamingStatusRegistry;

    public Flux<String> startInterviewStreaming(Long sessionId, LLMPromptType type, String message) {
        return Mono.defer(() ->
                        streamingStatusRegistry.isStreamingInProgress(sessionId)
                                .flatMap(inProgress -> {
                                    log.info("in progress: {}", inProgress);
                                    if (inProgress) {
                                        return Mono.error(StreamingAlreadyInProgressException::new);
                                    }
                                    return streamingStatusRegistry.setStreamStatus(sessionId, SessionStreamingStatusRegistry.StreamingStatus.IN_PROGRESS);
                                })
                )
                .thenMany(
                        interviewStreamer.stream(sessionId.intValue(), type, message)
                                .flatMap(partialResponse -> appendPartialResponse(sessionId, partialResponse).thenReturn(partialResponse))
                                .delayElements(java.time.Duration.ofMillis(10))
                                .doFinally(signalType -> {
                                    if (signalType == SignalType.ON_COMPLETE || signalType == SignalType.ON_ERROR || signalType == SignalType.CANCEL) {
                                        saveResponse(sessionId)
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .subscribe();
                                        streamingStatusRegistry.setStreamStatus(sessionId, SessionStreamingStatusRegistry.StreamingStatus.TERMINATED)
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .subscribe();
                                    }
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                );
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return bufferRegistry.retrieveMessageBuffer(sessionId);
    }

    private Mono<Void> appendPartialResponse(Long sessionId, String partialResponse) {
        return bufferRegistry.appendMessageBuffer(sessionId, partialResponse)
                .doOnSuccess(length -> log.debug("Room {}: Appended '{}', current length: {}", sessionId, partialResponse, length))
                .doOnError(error -> log.error("Room {}: Error appending '{}': {}", sessionId, partialResponse, error.getMessage()))
                .then();
    }

    private Mono<Void> saveResponse(Long sessionId) {
        return bufferRegistry.retrieveMessageBuffer(sessionId)
                .flatMap(fullResponse -> {
                    log.info("💬 스트림 완료 - 전체 응답 저장 시도: {}", fullResponse);
                    return messageService.saveMessage(sessionId, fullResponse, InterviewSender.LLM)
                            .then(bufferRegistry.clearMessageBuffer(sessionId));
                })
                .then();
    }
}
