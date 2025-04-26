package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.global.redis.RedisService;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import com.example.aiinterview.module.interview.domain.entity.InterviewSessionStatus;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewMessageRepository;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewRoomRepository;
import com.example.aiinterview.module.interviewer.InterviewStreamer;
import com.example.aiinterview.module.interviewer.prompt.LLMPromptType;
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
public class InterviewRoomApplicationService {
    private final InterviewStreamer interviewStreamer;
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
    public Mono<List<InterviewMessage>> retrieveMessage(Long roomId, Long memberId){
        return messageRepository.findByRoomId(roomId)
                .collectList()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }
    /**
     * 메시지 추가
     */
    public Flux<String> sendMessage(Long roomId, Long memberId, String message) {
        return roomRepository.findById(roomId)
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
                .flatMapMany(room -> saveHumanMessage(roomId, message)
                        .flatMapMany(savedHumanMessage -> startLLMStreaming(roomId,message)));
    }

    /**
     * 면접 종료
     */
    public Mono<Void> completeInterview(Long roomId, String feedback) {
        return roomRepository.findById(roomId)
                .flatMap(room -> {
//                    roomDomainService.completeInterview(room);
                    return roomRepository.save(room).then();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }

    // ==========================
    // 2. Private Helper Methods
    // ==========================
    private Flux<String> startLLMStreaming(Long roomId, String message) {
        return interviewStreamer.stream(roomId.intValue(), LLMPromptType.BACKEND, message)
                .doOnNext(partialResponse -> appendLLMResponsePart(roomId, partialResponse))
                .delayElements(java.time.Duration.ofSeconds(10))
                .doOnComplete(()->saveLLMResponse(roomId))
                .subscribeOn(Schedulers.boundedElastic());
    }
    private Mono<InterviewMessage> saveHumanMessage(Long roomId, String message) {
        return messageRepository.save(InterviewMessage.createByMember(roomId, message))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void appendLLMResponsePart(Long roomId, String partialBuffer) {
        String redisKey = String.format("interview:%d:partial_response", roomId);
        redisService.appendToString(redisKey, partialBuffer)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(length -> log.debug("Room {}: Appended '{}', current length: {}", roomId, partialBuffer, length))
                .doOnError(error -> log.error("Room {}: Error appending '{}': {}", roomId, partialBuffer, error.getMessage()))
                .subscribe();
    }

    private void saveLLMResponse(Long roomId) {

        String redisKey = String.format("interview:%d:partial_response", roomId);
        redisService.get(redisKey)
                .flatMap(fullResponse -> {
                    log.info("💬 스트림 완료 - 전체 응답 저장 시도: {}", fullResponse);
                    return messageRepository.save(InterviewMessage.createByLLM(roomId, fullResponse))
                            .then(redisService.delete(redisKey));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    public Mono<String> retrieveMessageHistory(Long roomId) {
        String redisKey = String.format("interview:%d:partial_response", roomId);
        return redisService.get(redisKey);
    }

    public Mono<List<InterviewSession>> retrieveInterviewRoom(Long memberId) {
        return roomRepository.findByIntervieweeId(memberId)
                .collectList()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }
}
