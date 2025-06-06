package com.example.aiinterview.module.interview.presentation;

import com.example.aiinterview.global.common.response.ResponseFactory;
import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.module.interview.application.InterviewApplicationService;
import com.example.aiinterview.module.interview.application.dto.*;
import com.example.aiinterview.module.interview.domain.entity.InterviewResultSummary;
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
@RequestMapping("/api/v1/interviews/{sessionId}/messages")
@RequiredArgsConstructor
@Tag(name = "4. INTERVIEW MESSAGE API", description = "인터뷰 메시지 관련 기능을 제공하는 API입니다.")
public class InterviewMessageController {
    private final InterviewApplicationService applicationService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션의 모든 채팅 기록 조회(토큰 필요)", description = "토큰을 포함해야 하며, 인터뷰 세션(방) 내부의 메시지 정보를 조회합니다.")
    public Mono<ApiResponse<InterviewMessageWithStatusResponse>> retrieveMessage(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                                                 @RequestAttribute("userId") Long memberId) {
        return applicationService.retrieveMessages(sessionId, memberId)
                .flatMap(ResponseFactory::successMono);
    }

    @GetMapping("/{messageId}/user-content")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션의 특정 사용자 답변 메시지를 조회 (토큰 필요)", description = "토큰을 포함해야 하며, 인터뷰 세션(방) 내부의 메시지 정보를 조회합니다.")
    public Mono<ApiResponse<UserContentResponse>> retrieveOnlyUserContent(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                                          @Parameter(description = "메시지 고유 ID") @PathVariable Long messageId,
                                                                          @RequestAttribute("userId") Long memberId) {
        return applicationService.retrieveUserContent(sessionId, messageId, memberId)
                .flatMap(ResponseFactory::successMono);
    }
    @GetMapping("/{messageId}/llm-content")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션의 특정 LLM 질뭊ㄴ 메시지를 조회(토큰 필요)", description = "토큰을 포함해야 하며, 인터뷰 세션(방) 내부의 메시지 정보를 조회합니다.")
    public Mono<ApiResponse<LlmContentResponse>> retrieveOnlyLlmContent(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                                                        @Parameter(description = "메시지 고유 ID") @PathVariable Long messageId,
                                                                                 @RequestAttribute("userId") Long memberId) {
        return applicationService.retrieveLlmContent(sessionId, messageId, memberId)
                .flatMap(ResponseFactory::successMono);
    }
//    @Deprecated
//    @SseMapping("/{sessionId}/messages/stream")
//    @Operation(summary = "AI 스트리밍 진행(토큰 필요)", description = "토큰이 쿼리 스트링으로 포함되어야함. (?token=value) 의 형식으로, 이를 통해서 실시간 스트리밍을 진행")
//    public Flux<SseResponse> sendMessage(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
//                                         @RequestAttribute("userId") Long userId,
//                                         @Parameter(description = "메시지") @RequestParam("message") String message) {
//        Flux<SseResponse> messageFlux = applicationService.saveUserMessageAndStreamingLLM(sessionId, userId, message).share();
//        Flux<SseResponse> heartbeatFlux = generateHeartbeatPing().takeUntilOther(messageFlux);
//        return Flux.merge(messageFlux, heartbeatFlux)
//                .concatWith(Mono.just(SseResponse.complete()));
//    }

    @PostMapping(value = "/stream", produces = "text/event-stream")
    @Operation(summary = "AI 스트리밍 진행(토큰 필요)", description = "토큰이 쿼리 스트링으로 포함되어야함. (?token=value) 의 형식으로, 이를 통해서 실시간 스트리밍을 진행")
    public Flux<SseResponse> saveLLMAndStreamQuestion(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                      @RequestAttribute("userId") Long userId,
                                                      @Parameter(description = "stream에 필요한 요소") @RequestBody() StreamRequest request) {
        Flux<SseResponse> messageFlux = applicationService.saveUserMessageAndStreamingLLM(sessionId, userId, request.getMessage())
                .share();
        Flux<SseResponse> heartbeatFlux = generateHeartbeatPing().takeUntilOther(messageFlux);
        return Flux.merge(messageFlux, heartbeatFlux)
                .concatWith(Mono.just(SseResponse.complete()));
    }

//    @Deprecated
//    @GetMapping("/{sessionId}/buffer")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "deprecated", description = "AI 스트리밍 진행 시 데이터를 유실해서, 받아오지 못할 때, buffer 의 데이터를 가지고 오면 됨")
//    public Mono<ApiResponse<String>> retrieveMessageHistory(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
//                                                            @RequestAttribute("userId") Long memberId){
//        return applicationService.retrieveMessageBuffer(sessionId)
//                .flatMap(ResponseFactory::successMono);
//    }
//
    private Flux<SseResponse> generateHeartbeatPing(){
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> SseResponse.heartbeat());
    }
}
