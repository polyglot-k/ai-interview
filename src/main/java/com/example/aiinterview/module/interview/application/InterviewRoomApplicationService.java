package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.domain.service.InterviewRoomDomainService;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewRoom;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewMessageRepository;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewRoomRepository;
import com.example.aiinterview.module.llm.LLMStreamer;
import com.example.aiinterview.module.llm.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewRoomApplicationService {
    private final InterviewRoomDomainService roomDomainService;
    private final LLMStreamer llmStreamer;
    private final InterviewRoomRepository roomRepository;
    private final InterviewMessageRepository messageRepository;

    // ==========================
    // 1. Public Methods
    // ==========================

    /**
     * 면접방 생성
     */
    public Mono<InterviewRoom> createRoom() {
        InterviewRoom room = InterviewRoom.create();
        return roomRepository.save(room);
    }

    /**
     * 메시지 추가
     */
    public Flux<String> sendMessage(Long roomId, Long memberId, String message) {
        return  roomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")))
                .flatMap(room -> {
                    if (!room.getMemberId().equals(memberId)) {
                        return Mono.error(new IllegalAccessException("접근 권한이 없습니다."));
                    }
                    return Mono.just(room);
                })
                .flatMapMany(room -> saveHumanMessage(roomId, message)
                        .flatMapMany(savedHumanMessage -> startLLMStreaming(roomId, message)));
    }

    /**
     * 면접 종료
     */
    public Mono<Void> completeInterview(Long roomId, String feedback) {
        return roomRepository.findById(roomId)
                .flatMap(room -> {
                    roomDomainService.completeInterview(room);
                    return roomRepository.save(room).then();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }

    // ==========================
    // 2. Private Helper Methods
    // ==========================
    private Flux<String> startLLMStreaming(Long roomId, String message) {
        List<String> responseBuffer = new ArrayList<>();
        return llmStreamer.stream(roomId.intValue(), LLMPromptType.BACKEND, message)
                .doOnNext(responseBuffer::add)
                .delayElements(java.time.Duration.ofMillis(300))
                .doOnComplete(() -> saveLLMResponse(roomId, responseBuffer))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<InterviewMessage> saveHumanMessage(Long roomId, String message) {
        return messageRepository.save(InterviewMessage.createByMember(roomId, message))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void saveLLMResponse(Long roomId, List<String> responseBuffer) {
        String fullResponse = String.join("", responseBuffer);
        log.info("💬 전체 응답 완료: {}", fullResponse);
        messageRepository.save(InterviewMessage.createByLLM(roomId, fullResponse))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
