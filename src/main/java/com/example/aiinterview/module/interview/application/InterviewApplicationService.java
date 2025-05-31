package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.application.dto.InterviewAnalysisPayload;
import com.example.aiinterview.module.interview.application.dto.InterviewMessageWithStatusResponse;
import com.example.aiinterview.module.interview.application.dto.SseResponse;
import com.example.aiinterview.module.interview.domain.vo.InterviewMessageWithStatus;
import com.example.aiinterview.module.interview.domain.vo.InterviewSender;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.service.InterviewSessionAuthorizationService;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewApplicationService {

    private static final int MAX_MESSAGE_COUNT = 20;

    private final InterviewSessionService sessionService;
    private final InterviewSessionAuthorizationService sessionAuthorizationService;
    private final InterviewMessageService messageService;
    private final LLMInterviewStreamingService llmInterviewStreamingService;
    private final InterviewAnalysisService analysisService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Mono<InterviewSession> createRoom(Long userId) {
        return sessionService.create(userId);
    }

    public Mono<List<InterviewSession>> retrieveInterviewSessions(Long userId) {
        return sessionService.findByMemberId(userId);
    }

    public Mono<InterviewMessageWithStatus> retrieveMessages(Long sessionId, Long userId) {
        return validateOwnedSessionByUserId(sessionId, userId)
                .flatMap(session -> messageService.retrieveMessageWithStatus(sessionId));
    }

    public Flux<SseResponse> processMessageAndStreamingLLM(Long sessionId, Long userId, String message) {
        return validateAccessSession(sessionId, userId)
                .flatMapMany(session -> messageService.saveMessage(sessionId, message, InterviewSender.USER)
                        .then(messageService.retrieveCount(sessionId))
                        .flatMapMany(count -> handleLLMStreamingOrSessionCompletion(sessionId, message, count))
                );
    }

    public Mono<Void> completeAndAnalyze(Long sessionId) {
        return messageService.deleteLastMessage(sessionId)
                .then(sessionService.complete(sessionId))
                .then(sendMessageReactive(InterviewAnalysisPayload.of(sessionId)));
    }

    public Mono<Void> analyze(Long sessionId) {
        return sendMessageReactive(InterviewAnalysisPayload.of(sessionId));
    }


    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return llmInterviewStreamingService.retrieveMessageBuffer(sessionId);
    }
    private Mono<Void> sendMessageReactive(Object payload) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend("task.queue",payload))
                .subscribeOn(Schedulers.boundedElastic()) // 블로킹 호출 방지
                .then();
    }


    private Mono<InterviewSession> validateAccessSession(Long sessionId, Long userId) {
        return sessionService.findById(sessionId)
                .flatMap(session -> sessionAuthorizationService.validateAccess(session, userId)
                        .thenReturn(session));
    }

    private Mono<InterviewSession> validateOwnedSessionByUserId(Long sessionId, Long userId) {
        return sessionService.findById(sessionId)
                .flatMap(session -> sessionAuthorizationService.assertUserIsOwner(session, userId)
                        .thenReturn(session));
    }
    private Flux<SseResponse> handleLLMStreamingOrSessionCompletion(Long sessionId, String message, long currentMessageCount) {
        if (currentMessageCount >= MAX_MESSAGE_COUNT) {
            return sessionService.complete(sessionId)
                    .thenReturn(SseResponse.terminated())
                    .flux();
        }
        return llmInterviewStreamingService
                .startInterviewStreaming(sessionId, LLMPromptType.BACKEND, message)
                .map(SseResponse::progress);
    }
}