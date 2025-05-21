package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.application.dto.SseResponse;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.service.InterviewSessionAuthorizationService;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @Transactional
    public Mono<InterviewSession> createRoom(Long userId) {
        return sessionService.create(userId);
    }

    @Transactional(readOnly = true)
    public Mono<List<InterviewSession>> retrieveInterviewSessions(Long userId) {
        return sessionService.findByMemberId(userId);
    }

    @Transactional(readOnly = true)
    public Mono<List<InterviewMessage>> retrieveMessages(Long sessionId, Long userId) {
        return validateOwnedSessionByUserId(sessionId, userId)
                .flatMap(session -> messageService.retrieveMessage(sessionId));
    }

    @Transactional
    public Flux<SseResponse> processMessageAndStreamingLLM(Long sessionId, Long userId, String message) {
        return validateAccessSession(sessionId, userId)
                .flatMapMany(session -> messageService.saveMessage(sessionId, message, InterviewSender.USER)
                        .then(messageService.retrieveCount(sessionId))
                        .flatMapMany(count -> handleLLMStreamingOrSessionCompletion(sessionId, message, count))
                );
    }

    @Transactional
    public Mono<Void> completeAndAnalyze(Long sessionId) {
        return messageService.deleteLastMessage(sessionId) //스트리밍 정보 제거
                .then(sessionService.complete(sessionId))
                .then(messageService.retrieveMessage(sessionId))
                .flatMap(messages ->  analysisService.analyze(sessionId, Flux.fromIterable(messages)));
    }

    @Transactional
    public Mono<Void> analyze(Long sessionId) {
        return messageService.retrieveMessage(sessionId)
                .flatMap(messages -> analysisService.analyze(sessionId, Flux.fromIterable(messages)));
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return llmInterviewStreamingService.retrieveMessageBuffer(sessionId);
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