package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.global.redis.RedisService;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.entity.InterviewSessionStatus;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewMessageRepository;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewRoomRepository;
import com.example.aiinterview.module.llm.analysis.InterviewSessionAnalyzer;
import com.example.aiinterview.module.llm.interviewer.InterviewStreamer;
import com.example.aiinterview.module.llm.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewSessionApplicationService {
    private final InterviewStreamer interviewStreamer;
    private final InterviewSessionAnalyzer analyzer;
    private final InterviewRoomRepository roomRepository;
    private final InterviewMessageRepository messageRepository;
    private final RedisService redisService;
    // ==========================
    // 1. Public Methods
    // ==========================
    /**
     * 면접방 생성
     */
    public Mono<InterviewSession> createRoom(Long memberId) {
        InterviewSession room = InterviewSession.create(memberId);
        return roomRepository.save(room);
    }

    /**
     * 메시지 가져오기
     */
    public Mono<List<InterviewMessage>> retrieveMessage(Long sessionId, Long memberId){
        return messageRepository.findBySessionId(sessionId)
                .collectList()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }
    /**
     * 메시지 추가
     */
    public Flux<String> sendMessage(Long sessionId, Long memberId, String message) {
        return roomRepository.findById(sessionId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")))
                .flatMap(room -> {
                    if (!room.getIntervieweeId().equals(memberId)) {
                        return Mono.error(new IllegalAccessException("접근 권한이 없습니다."));
                    }
                    if (InterviewSessionStatus.ENDED.equals(room.getStatus())) {
                        return Mono.error(new IllegalArgumentException("이미 종료 된 면접 입니다."));
                    }
                    return Mono.just(room);
                })
                .flatMapMany(room -> saveHumanMessage(sessionId, message)
                        .flatMapMany(savedHumanMessage -> startLLMStreaming(sessionId,message)));
    }

    /**
     * 면접 종료
     */
    public Mono<Void> completeInterview(Long sessionId, String feedback) {
        return roomRepository.findById(sessionId)
                .flatMap(room -> {
//                    roomDomainService.completeInterview(room);
                    return roomRepository.save(room).then();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }

    public Mono<String> retrieveMessageBuffer(Long sessionId) {
        String redisKey = String.format("interview:%d:partial_response", sessionId);
        return redisService.get(redisKey);
    }

    public Mono<List<InterviewSession>> retrieveInterviewRoom(Long memberId) {
        return roomRepository.findByIntervieweeId(memberId)
                .collectList()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }
    public Mono<Void> analyze(Long sessionId){
        return analyzer.analyze(sessionId)
                .then();
    }

    // ==========================
    // 2. Private Helper Methods
    // ==========================
    private Flux<String> startLLMStreaming(Long sessionId, String message) {
        return interviewStreamer.stream(sessionId.intValue(), LLMPromptType.BACKEND, message)
                .doOnNext(partialResponse -> appendLLMResponsePart(sessionId, partialResponse))
                .delayElements(java.time.Duration.ofMillis(50))
                .doOnComplete(()->saveLLMResponse(sessionId))
                .subscribeOn(Schedulers.boundedElastic());
    }
    private Mono<InterviewMessage> saveHumanMessage(Long sessionId, String message) {
        return messageRepository.save(InterviewMessage.createByMember(sessionId, message))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void appendLLMResponsePart(Long sessionId, String partialBuffer) {
        String redisKey = String.format("interview:%d:partial_response", sessionId);
        redisService.appendToString(redisKey, partialBuffer)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(length -> log.debug("Room {}: Appended '{}', current length: {}", sessionId, partialBuffer, length))
                .doOnError(error -> log.error("Room {}: Error appending '{}': {}", sessionId, partialBuffer, error.getMessage()))
                .subscribe();
    }

    private void saveLLMResponse(Long sessionId) {

        String redisKey = String.format("interview:%d:partial_response", sessionId);
        redisService.get(redisKey)
                .flatMap(fullResponse -> {
                    log.info("💬 스트림 완료 - 전체 응답 저장 시도: {}", fullResponse);
                    return messageRepository.save(InterviewMessage.createByLLM(sessionId, fullResponse))
                            .then(redisService.delete(redisKey));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
