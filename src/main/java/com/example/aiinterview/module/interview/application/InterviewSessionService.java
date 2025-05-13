package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.application.dto.SseResponse;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.entity.InterviewSessionStatus;
import com.example.aiinterview.module.interview.domain.service.InterviewSessionAuthorizationService;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewSessionRepository;
import com.example.aiinterview.module.llm.analysis.InterviewSessionAnalyzer;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    public Mono<InterviewSession> createRoom(Long memberId) {
        InterviewSession room = InterviewSession.create(memberId);
        return sessionRepository.save(room);
    }

    /**
     * 메시지 가져오기
     */
    public Mono<List<InterviewMessage>> retrieveMessage(Long sessionId, Long memberId){
        return messageService.retrieveMessage(sessionId,memberId);
    }
    /**
     * 메시지 추가
     */
    public Flux<SseResponse> sendMessageAndStreamingLLM(Long sessionId, Long memberId, String message) {
        return validateSessionAndAuthorization(sessionId, memberId)
                .flatMapMany(session -> processMessage(session, message));
    }

    /**
     * 면접 종료
     */
    public Mono<Void> completeInterview(Long sessionId, String feedback) {
        return sessionRepository.findById(sessionId)
                .flatMap(room -> {
//                    roomDomainService.completeInterview(room);
                    return sessionRepository.save(room).then();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        return LLMInterviewStreamingService.retrieveMessageBuffer(sessionId);
    }

    public Mono<List<InterviewSession>> retrieveInterviewRoom(Long memberId) {
        return sessionRepository.findByIntervieweeId(memberId)
                .collectList()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }
    public Mono<Void> analyze(Long sessionId){
        return analyzer.analyze(sessionId)
                .then();
    }

    private Mono<InterviewSession> validateSessionAndAuthorization(Long sessionId, Long memberId) {
        return sessionRepository.findById(sessionId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")))
                .flatMap(session -> sessionAuthorizationService
                        .validateSessionAccess(session, memberId)
                        .thenReturn(session));
    }
    private Flux<SseResponse> processMessage(InterviewSession session, String message) {
        return saveHumanMessage(session.getId(), message)
                .flatMapMany(saved ->
                        LLMInterviewStreamingService.startInterviewStreaming(session.getId(), LLMPromptType.BACKEND, message)
                                .map(SseResponse::progress)
                );
    }

    private Mono<InterviewMessage> saveHumanMessage(Long sessionId, String message) {
        return messageService.saveMessage(sessionId, message, InterviewSender.USER);
    }
}
