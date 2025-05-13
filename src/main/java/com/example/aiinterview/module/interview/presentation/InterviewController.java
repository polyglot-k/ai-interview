package com.example.aiinterview.module.interview.presentation;

import com.example.aiinterview.module.interview.application.InterviewSessionApplicationService;
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
    private final InterviewSessionApplicationService applicationService;

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
    public Flux<String> sendMessage(@PathVariable Long sessionId,
                                    @RequestAttribute("userId") Long userId,
                                    @RequestParam("message") String message) {
        Flux<String> messageFlux = applicationService.sendMessage(sessionId,  userId, message);
        Flux<String> heartbeatFlux = Flux.interval(Duration.ofSeconds(1))
                .map(tick -> "heartbeat: ping")
                .takeUntilOther(messageFlux.ignoreElements());

        return Flux.merge(messageFlux, heartbeatFlux);
    }


    @GetMapping("/{sessionId}/buffer")
    public Mono<String> retrieveMessageHistory(@PathVariable Long sessionId,
                                               @RequestAttribute("userId") Long memberId){
        return applicationService.retrieveMessageBuffer(sessionId);
    }
}
