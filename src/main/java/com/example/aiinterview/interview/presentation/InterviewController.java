package com.example.aiinterview.interview.presentation;

import com.example.aiinterview.interview.application.InterviewRoomApplicationService;
import com.example.aiinterview.interview.infrastructure.InterviewRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewRoomApplicationService applicationService;
    private final InterviewRoomRepository interviewRoomRepository;

    @GetMapping
    public void a() {
        applicationService.createRoom();
    }
    @PostMapping("/{roomId}/sendMessage")
    public Flux<String> sendMessage(@PathVariable Long roomId, @RequestBody String message) {
        // InterviewRoomApplicationService의 sendMessage 호출
        return applicationService.sendMessage(roomId, message);
    }
}
