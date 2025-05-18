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

    private final InterviewSessionService sessionService;
    private final InterviewSessionAuthorizationService sessionAuthorizationService;
    private final InterviewMessageService messageService;
    private final LLMInterviewStreamingService llmInterviewStreamingService;
    private final InterviewAnalysisService analysisService;

    @Transactional
    public Mono<InterviewSession> createRoom(Long memberId) {
        return sessionService.create(memberId);
    }

    @Transactional(readOnly = true)
    public Mono<List<InterviewSession>> retrieveInterviewSessions(Long memberId) {
        return sessionService.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public Mono<List<InterviewMessage>> retrieveMessages(Long sessionId, Long memberId) {
        return validateSessionAndAuthorization(sessionId, memberId)
                .flatMap(session -> messageService.retrieveMessage(sessionId));
    }

    @Transactional
    public Flux<SseResponse> processMessageAndStreamingLLM(Long sessionId, Long memberId, String message) {
        return validateSessionAndAuthorization(sessionId, memberId)
                .flatMapMany(session ->
                        messageService.saveMessage(sessionId, message, InterviewSender.USER)
                                .then(messageService.retrieveCount(sessionId)) // 🔹 저장 후 카운트 확인
                                .flatMapMany(count -> {
                                    if (count >= 20) {
                                            return sessionService.complete(sessionId)
                                                    .thenReturn(SseResponse.terminated())
                                                    .flux();
                                    } else {
                                        return llmInterviewStreamingService
                                                .startInterviewStreaming(sessionId, LLMPromptType.BACKEND, message)
                                                .map(SseResponse::progress);
                                    }
                                })
                );
    }


    public Mono<Void> analyze(Long sessionId) {
        return Mono.empty(); // analysisService.analyze(sessionId).then();
    }

    public Mono<Void> completeInterview(Long sessionId) {
        return messageService.deleteLastMessage(sessionId) //스트리밍 정보 제거
                .then(sessionService.complete(sessionId))
                .then(analyze(sessionId));
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return llmInterviewStreamingService.retrieveMessageBuffer(sessionId);
    }

    private Mono<InterviewSession> validateSessionAndAuthorization(Long sessionId, Long memberId) {
        return sessionService.findById(sessionId)
                .flatMap(session -> sessionAuthorizationService.validateSessionAccess(session, memberId)
                        .thenReturn(session));
    }
}