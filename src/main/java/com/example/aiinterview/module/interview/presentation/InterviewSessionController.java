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
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
@Tag(name = "3. INTERVIEW SESSION API", description = "인터뷰 관련 기능을 제공하는 API입니다.")
public class InterviewSessionController {
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
    public Mono<ApiResponse<InterviewSession>> createSessionAndStartStream(@RequestAttribute("userId") Long memberId) {
        return applicationService.createRoom(memberId)
                .flatMap(ResponseFactory::successMono);
    }

    @PostMapping("/{sessionId}/complete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션을 종료", description = "토큰을 포함해야하며, 인터뷰 세션을 종료합니다.")
    public Mono<ApiResponse<Void>> completeSession(@PathVariable("sessionId") Long sessionId){
        return applicationService.completeAndAnalyze(sessionId)
                .then(ResponseFactory.successVoid());
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 세션을 제거", description = "토큰을 포함해야하며, 인터뷰 세션을 삭제합니다.(영구적으로)")
    public Mono<ApiResponse<Void>> deleteSession(@PathVariable("sessionId") Long sessionId){
        return applicationService.deleteSession(sessionId)
                .then(ResponseFactory.successVoid());
    }
}
