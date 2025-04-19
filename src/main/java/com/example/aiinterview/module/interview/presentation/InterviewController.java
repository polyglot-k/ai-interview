package com.example.aiinterview.module.interview.presentation;

import com.example.aiinterview.module.interview.application.InterviewRoomApplicationService;
import com.example.aiinterview.module.interview.domain.entity.InterviewRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewRoomApplicationService applicationService;

    @PostMapping()
    public Mono<InterviewRoom> createRoom() {
        return applicationService.createRoom();
    }

    /**
     * 해당 유저가 속한 방 아니면 접근 못하게
     * @param roomId
     * @param message
     * @return
     */
    @PostMapping("/{roomId}/message")
    public Flux<String> sendMessage(@PathVariable Long roomId,
                                    @RequestAttribute("userId") Long memberId,
                                    @RequestBody String message) {
        // InterviewRoomApplicationService의 sendMessage 호출
        return applicationService.sendMessage(roomId, memberId, message);
    }

    @GetMapping("/{roomId}/history")
    public Flux<String> retrieveMessageHistory(@PathVariable Long roomId){
        return Flux.never();
    }
}
