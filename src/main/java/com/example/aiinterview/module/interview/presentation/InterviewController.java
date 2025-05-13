package com.example.aiinterview.module.interview.presentation;

import com.example.aiinterview.module.interview.application.InterviewSessionService;
import com.example.aiinterview.module.interview.application.dto.SseResponse;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewSessionService applicationService;

    @GetMapping()
    public Mono<List<InterviewSession>> retrieveInterviewRoom(@RequestAttribute("userId") Long memberId){
        return applicationService.retrieveInterviewRoom(memberId);
    }
    @PostMapping()
    public Mono<InterviewSession> createRoom(@RequestAttribute("userId") Long userId) {
        return applicationService.createRoom(userId);
    }

    @GetMapping("/{sessionId}/messages")
    public Mono<List<InterviewMessage>> retrieveMessage(@PathVariable Long sessionId,
                                                        @RequestAttribute("userId") Long memberId) {
        return applicationService.retrieveMessage(sessionId, memberId);
    }

    /**
     * 해당 유저가 속한 방 아니면 접근 못하게
     * @param sessionId
     * @param message
     * @return
     */
    @GetMapping(value = "/{sessionId}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SseResponse> sendMessage(@PathVariable Long sessionId,
                                         @RequestAttribute("userId") Long userId,
                                         @RequestParam("message") String message) {
        Flux<SseResponse> messageFlux = applicationService.sendMessageAndStreamingLLM(sessionId, userId, message);
        Flux<SseResponse> heartbeatFlux = generateHeartbeatPing().takeUntilOther(messageFlux);
        return Flux.merge(messageFlux, heartbeatFlux)
                .concatWith(Mono.just(SseResponse.complete()));
    }


    @GetMapping("/{sessionId}/buffer")
    public Mono<String> retrieveMessageHistory(@PathVariable Long sessionId,
                                               @RequestAttribute("userId") Long memberId){
        return applicationService.retrieveMessageBuffer(sessionId);
    }
    private Flux<SseResponse> generateHeartbeatPing(){
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> SseResponse.heartbeat());

    }
}
