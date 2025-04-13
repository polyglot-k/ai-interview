package com.example.aiinterview.interview.application;

import com.example.aiinterview.interview.infrastructure.InterviewRoomRepository;
import com.example.aiinterview.interview.domain.InterviewRoomDomainService;
import com.example.aiinterview.interview.domain.model.InterviewMessage;
import com.example.aiinterview.interview.domain.model.InterviewRoom;
import com.example.aiinterview.llm.LLMStreamer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewRoomApplicationService {
    private final InterviewRoomDomainService roomDomainService;
    private final LLMStreamer llmStreamer;
    private final InterviewRoomRepository repository;

    /**
     * 면접방 생성
     */
    public InterviewRoom createRoom() {
        InterviewRoom room = InterviewRoom.create();
        return repository.save(room);
    }

    /**
     * 메시지 추가
     */
    public Flux<String> sendMessage(Long roomId, String message) {
        List<String> collectedMessages = new ArrayList<>();

        return Mono.fromCallable(() -> repository.findById(roomId))
                .publishOn(Schedulers.boundedElastic()) // Offload repository lookup
                .flatMapMany(optionalRoom ->
                        llmStreamer.stream(message)
                                .doOnNext(collectedMessages::add) // Collect each streamed message
                                .doOnTerminate(() -> {
                                    Mono.fromRunnable(() -> {
                                                String fullText = String.join("", collectedMessages);

                                                InterviewRoom room = optionalRoom.orElseThrow(
                                                        RuntimeException::new
                                                );
                                                InterviewMessage llmMessage = InterviewMessage.createByLLM(room, fullText);
                                                roomDomainService.sendMessage(room, llmMessage);
                                                repository.save(room);
                                                try {
                                                    Thread.sleep(5000); // Simulate asynchronous I/O operation
                                                    System.out.println("io job completed");
                                                } catch (InterruptedException e) {
                                                    Thread.currentThread().interrupt();
                                                }
                                            })
                                            .subscribeOn(Schedulers.boundedElastic()) // Run DB save on a separate thread
                                            .subscribe();
                                })
                );
    }
    /**
     * 면접 종료
     */
    @Transactional
    public void completeInterview(Long roomId, String feedback) {
        InterviewRoom room = repository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("면접방이 존재하지 않습니다."));

        roomDomainService.completeInterview(room);
    }
}
