package com.example.aiinterview.module.interview.presentation;

import com.example.aiinterview.global.common.response.ResponseFactory;
import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.global.sse.SseMapping;
import com.example.aiinterview.module.interview.application.InterviewApplicationService;
import com.example.aiinterview.module.interview.application.dto.SseResponse;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
@RestController
@Slf4j
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
@Tag(name = "INTERVIEW API", description = "인터뷰 관련 기능을 제공하는 API입니다.")
public class InterviewController {
    private final InterviewApplicationService applicationService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션 조회(토큰 필요)", description = "토큰을 포함해야 하며, 인터뷰 세션(방) 을 조회합니다.")
    public Mono<ApiResponse<List<InterviewSession>>> retrieveInterviewSession(@RequestAttribute("userId") Long memberId){
        return applicationService.retrieveInterviewSessions(memberId)
                .flatMap(ResponseFactory::successMono);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "인터뷰 세션 생성(토큰 필요)", description = "토큰을 포함해야 하며, 인터뷰 세션(방) 을 생성합니다.")
    public Mono<ApiResponse<InterviewSession>> createSession(@RequestAttribute("userId") Long memberId) {
        return applicationService.createRoom(memberId)
                .flatMap(ResponseFactory::successMono);
    }

    @PostMapping("/{sessionId}/complete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션을 종료", description = "토큰을 포함해야하며, 인터뷰 세션을 종료합니다.")
    public Mono<ApiResponse<Void>> completeSession(@PathVariable("sessionId") Long sessionId){
        return applicationService.completeInterview(sessionId)
                .then(ResponseFactory.successVoid());
    }

    @GetMapping("/{sessionId}/messages")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션의 모든 채팅 기록 조회(토큰 필요)", description = "토큰을 포함해야 하며, 인터뷰 세션(방) 내부의 메시지 정보를 조회합니다.")
    public Mono<ApiResponse<List<InterviewMessage>>> retrieveMessage(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                                     @RequestAttribute("userId") Long memberId) {
        return applicationService.retrieveMessages(sessionId, memberId)
                .flatMap(ResponseFactory::successMono);
    }

    @SseMapping("/{sessionId}/messages/stream")
    @Operation(summary = "AI 스트리밍 진행(토큰 필요)", description = "토큰이 쿼리 스트링으로 포함되어야함. (?token=value) 의 형식으로, 이를 통해서 실시간 스트리밍을 진행")
    public Flux<SseResponse> sendMessage(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                         @RequestAttribute("userId") Long userId,
                                         @Parameter(description = "메시지") @RequestParam("message") String message) {
        Flux<SseResponse> messageFlux = applicationService.processMessageAndStreamingLLM(sessionId, userId, message).share();
        Flux<SseResponse> heartbeatFlux = generateHeartbeatPing().takeUntilOther(messageFlux);
        return Flux.merge(messageFlux, heartbeatFlux)
                .concatWith(Mono.just(SseResponse.complete()));
    }

    @Deprecated
    @GetMapping("/{sessionId}/buffer")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "deprecated", description = "AI 스트리밍 진행 시 데이터를 유실해서, 받아오지 못할 때, buffer 의 데이터를 가지고 오면 됨")
    public Mono<ApiResponse<String>> retrieveMessageHistory(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                            @RequestAttribute("userId") Long memberId){
        return applicationService.retrieveMessageBuffer(sessionId)
                .flatMap(ResponseFactory::successMono);
    }


    private Flux<SseResponse> generateHeartbeatPing(){
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> SseResponse.heartbeat());
    }
}
