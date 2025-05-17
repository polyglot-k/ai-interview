package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.application.dto.SseResponse;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.service.InterviewSessionAuthorizationService;
import com.example.aiinterview.module.interview.exception.InterviewSessionNotFoundException;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewSessionRepository;
import com.example.aiinterview.module.llm.analysis.InterviewSessionAnalyzer;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewSessionService {
    private final InterviewSessionAnalyzer analyzer;
    private final LLMInterviewStreamingService LLMInterviewStreamingService;
    private final InterviewMessageService messageService;
    private final InterviewSessionRepository sessionRepository;
    private final InterviewSessionAuthorizationService sessionAuthorizationService;
    // ==========================
    // 1. Public Methods
    // ==========================

    /**
     * 면접방 생성
     */
    @Transactional()
    public Mono<InterviewSession> createRoom(Long memberId) {
        InterviewSession room = InterviewSession.create(memberId);
        return sessionRepository.save(room);
    }

    /**
     * 메시지 가져오기
     */
    @Transactional(readOnly = true)
    public Mono<List<InterviewMessage>> retrieveMessage(Long sessionId, Long memberId) {
        return messageService.retrieveMessage(sessionId, memberId);
    }


    @Transactional(readOnly = true)
    public Mono<List<InterviewSession>> retrieveInterviewSession(Long memberId) {
        return sessionRepository.findByIntervieweeId(memberId)
                .collectList()
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new));
    }

    @Transactional()
    public Mono<Void> analyze(Long sessionId) {
        return analyzer.analyze(sessionId)
                .then();
    }

    /**
     * 메시지 추가
     */
    @Transactional()
    public Flux<SseResponse> processMessageAndStreamingLLM(Long sessionId, Long memberId, String message) {
        return validateSessionAndAuthorization(sessionId, memberId)
                .flatMapMany(session -> saveHumanMessage(session.getId(), message)
                        .flatMapMany(saved -> LLMInterviewStreamingService.startInterviewStreaming(session.getId(), LLMPromptType.BACKEND, message)
                                    .map(SseResponse::progress))
                );
    }

    /**
     * 면접 종료
     */
    public Mono<Void> completeInterview(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .flatMap(session -> {
                    session.end();
                    return sessionRepository.save(session).then();
                })
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new));
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return LLMInterviewStreamingService.retrieveMessageBuffer(sessionId);
    }

    private Mono<InterviewSession> validateSessionAndAuthorization(Long sessionId, Long memberId) {
        return sessionRepository.findById(sessionId)
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new))
                .flatMap(session -> sessionAuthorizationService
                        .validateSessionAccess(session, memberId)
                        .thenReturn(session));
    }

    private Mono<InterviewMessage> saveHumanMessage(Long sessionId, String message) {
        return messageService.saveMessage(sessionId, message, InterviewSender.USER);
    }
}
