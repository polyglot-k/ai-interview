package com.example.aiinterview.module.interview.presentation;

import com.example.aiinterview.global.common.response.ResponseFactory;
import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.module.interview.application.InterviewApplicationService;
import com.example.aiinterview.module.interview.application.dto.InterviewFeedbackResult;
import com.example.aiinterview.module.interview.domain.entity.InterviewResultSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
@RestController
@Slf4j
@RequestMapping("/api/v1/interviews/{sessionId}/feedbacks")
@RequiredArgsConstructor
@Tag(name = "5. INTERVIEW FEEDBACK API", description = "인터뷰 피드백 ㄹ관련 기능을 제공하는 API입니다.")
public class InterviewFeedbackController {
    private final InterviewApplicationService applicationService;

    @GetMapping("/result")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "sessoin 내의 특정 message 에 대한 피드백 상세 결과 조회 ", description = "sessoin 내의 특정 message 에 대한 피드백 상세 결과 조회 ")
    public Mono<ApiResponse<InterviewFeedbackResult>> retrieveFeedbackResult(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                                                    @Parameter(description = "메시지 고유 ID") @RequestParam("message_id") Long messageId,
                                                                                    @RequestAttribute("userId") Long memberId){
        return applicationService.retrieveFeedbackResult(sessionId,messageId, memberId)
                .flatMap(ResponseFactory::successMono);
    }

    @GetMapping("/total-result")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "sessoin 내의 특정 message 에 대한 총괄 피드백 결과 조회(평균 지표,토큰 필요)", description = "sessoin 내의 특정 message 에 대한 총괄 피드백 결과 조회(평균 지표,토큰 필요)")
    public Mono<ApiResponse<InterviewResultSummary>> retrieveFeedbackResult(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                                            @RequestAttribute("userId") Long memberId){
        return applicationService.retrieveFeedbackTotalResult(sessionId, memberId)
                .flatMap(ResponseFactory::successMono);
    }
    @GetMapping("/overviews")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인터뷰 session 피드백이 있는 메시지 목록", description = "AI 스트리밍 진행 시 데이터를 유실해서, 받아오지 못할 때, buffer 의 데이터를 가지고 오면 됨")
    public Mono<ApiResponse<?>> retrieveFeedbackOverviews(@Parameter(description = "세션 고유 ID") @PathVariable Long sessionId,
                                                                                    @RequestAttribute("userId") Long memberId){
        return applicationService.retrieveFeedbackOverviews(sessionId, memberId)
                .flatMap(ResponseFactory::successMono);
    }
}
