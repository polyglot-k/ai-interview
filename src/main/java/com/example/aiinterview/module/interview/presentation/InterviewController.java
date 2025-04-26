package com.example.aiinterview.module.interview.presentation;

import com.example.aiinterview.module.interview.application.InterviewRoomApplicationService;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewRoomApplicationService applicationService;

    @GetMapping()
    public Mono<List<InterviewSession>> retrieveInterviewRoom(@RequestAttribute("userId") Long memberId){
        return applicationService.retrieveInterviewRoom(memberId);
    }
    @PostMapping()
    public Mono<InterviewSession> createRoom(@RequestParam("userId") Long memberId) {
        return applicationService.createRoom(memberId);
    }

    @GetMapping("/{sessionId}/message")
    public Mono<List<InterviewMessage>> retrieveMessage(@PathVariable Long sessionId,
                                                        @RequestAttribute("userId") Long memberId) {
        // InterviewRoomApplicationService의 sendMessage 호출
        return applicationService.retrieveMessage(sessionId, memberId);
    }

    /**
     * 해당 유저가 속한 방 아니면 접근 못하게
     * @param sessionId
     * @param message
     * @return
     */
    @PostMapping("/{sessionId}/message")
    public Flux<String> sendMessage(@PathVariable Long sessionId,
                                    @RequestAttribute("userId") Long memberId,
                                    @RequestBody String message) {
        // InterviewRoomApplicationService의 sendMessage 호출
        return applicationService.sendMessage(sessionId, memberId, message);
    }

    @GetMapping("/{sessionId}/history")
    public Mono<String> retrieveMessageHistory(@PathVariable Long sessionId,
                                               @RequestAttribute("userId") Long memberId){
        return applicationService.retrieveMessageHistory(sessionId);
    }
}
