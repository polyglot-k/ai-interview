package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.application.dto.*;
import com.example.aiinterview.module.interview.domain.entity.InterviewResultSummary;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.service.InterviewSessionAuthorizationService;
import com.example.aiinterview.module.interview.domain.vo.InterviewDetailFeedbackOverview;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewApplicationService {

    private static final int MAX_MESSAGE_COUNT = 20;

    private final InterviewSessionService sessionService;
    private final InterviewSessionAuthorizationService sessionAuthorizationService;
    private final InterviewMessageService messageService;
    private final LLMInterviewStreamingService llmInterviewStreamingService;
    private final InterviewAnalysisService analysisService;
    private final InterviewFeedbackService feedbackService;
    @Transactional
    public Mono<InterviewSession> createRoom(Long userId) {
        return sessionService.create(userId)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(session ->
                        messageService.saveQuestionByLLM(session.getId(), "안녕하세요! 인터뷰를 시작하기 위해 자기 소개 및 자신이 지원한 직렬, 경력을 말씀해주세요.")
                                .thenReturn(session)
                );
    }

    public Mono<List<InterviewSession>> retrieveInterviewSessions(Long userId) {
        return sessionService.findByMemberId(userId);
    }

    public Mono<InterviewMessageWithStatusResponse> retrieveMessages(Long sessionId, Long userId) {
        return validateOwnedSessionByUserId(sessionId, userId)
                .flatMap(session -> messageService.retrieveMessageWithStatus(sessionId));
    }
    public Mono<UserContentResponse> retrieveUserContent(Long sessionId, Long messageId, Long userId) {
        return validateOwnedSessionByUserId(sessionId, userId)
                .flatMap(session -> messageService.retrieveUserContent(messageId))
                .map(UserContentResponse::of);
    }

    public Mono<LlmContentResponse> retrieveLlmContent(Long sessionId, Long messageId, Long userId) {
        return validateOwnedSessionByUserId(sessionId, userId)
                .flatMap(session -> messageService.retrieveLlmContent(messageId))
                .map(LlmContentResponse::of);
    }

    public Mono<InterviewFeedbackResult> retrieveFeedbackResult(Long sessionId, Long messageId, Long userId){
        return validateOwnedSessionByUserId(sessionId, userId)
                .flatMap(session -> feedbackService.retrieveFeedbackResultByMessageId(messageId));
    }
    public Mono<InterviewResultSummary> retrieveFeedbackTotalResult(Long sessionId, Long memberId) {
        return validateOwnedSessionByUserId(sessionId, memberId)
                .flatMap(session -> feedbackService.retrieveFeedbackTotalResultByMessageId(sessionId));
    }
    /**
     * 메시지 처리기
     * @param sessionId
     * @param userId
     * @param message
     * @return
     */
    public Flux<SseResponse> saveUserMessageAndStreamingLLM(Long sessionId, Long userId, String message) {
        return validateAccessSession(sessionId, userId)
                .flatMapMany(session ->
                    messageService.saveAnswerByUser(sessionId,message)
                            .then(messageService.retrieveCount(sessionId))
                            .flatMapMany(count -> handleLLMStreamingOrSessionCompletion(sessionId, message, count))
                );
    }

    public Mono<List<InterviewDetailFeedbackOverview>> retrieveFeedbackOverviews(Long sessionId, Long userId) {
        return feedbackService.retrieveFeedbackOverviews(sessionId)
                .collectList();
    }

    public Mono<Void> completeAndAnalyze(Long sessionId) {
        return sessionService.complete(sessionId)
                .then(messageService.retrieveMessage(sessionId))
                .flatMap(messages ->  analysisService.analyze(sessionId, Flux.fromIterable(messages)));
    }

    public Mono<Void> analyze(Long sessionId) {
        return messageService.retrieveMessage(sessionId)
                .flatMap(messages -> analysisService.analyze(sessionId, Flux.fromIterable(messages)));
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return llmInterviewStreamingService.retrieveMessageBuffer(sessionId);
    }
//    private Mono<Void> sendMessageReactive(Object payload) {
//        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend("task.queue",payload))
//                .subscribeOn(Schedulers.boundedElastic()) // 블로킹 호출 방지
//                .then();

//    }


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
                    .then(analyze(sessionId))
                    .thenReturn(SseResponse.terminated())
                    .flux();
        }
        return llmInterviewStreamingService
                .startInterviewStreaming(sessionId, LLMPromptType.BACKEND, message)
                .map(SseResponse::progress);
    }

}