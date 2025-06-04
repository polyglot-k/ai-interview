package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.application.dto.InterviewFeedbackResult;
import com.example.aiinterview.module.interview.domain.entity.InterviewResultSummary;
import com.example.aiinterview.module.interview.domain.repository.InterviewDetailFeedbackRepository;
import com.example.aiinterview.module.interview.domain.repository.InterviewResultSummaryRepository;
import com.example.aiinterview.module.interview.domain.vo.InterviewDetailFeedbackOverview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewFeedbackService {
    private final InterviewDetailFeedbackRepository detailFeedbackRepository;
    private final InterviewResultSummaryRepository resultSummaryRepository;

//    @Deprecated
//    public Mono<InterviewTotalFeedbackResponse> retrieveInterviewFeedback(Long sessionId) {
//        // Fetch the list of InterviewDetailFeedback asynchronously
//        Mono<List<InterviewSessionDetailFeedBackResponse>> detailFeedbacksMono =
//                detailFeedbackRepository.findFeedbacksByInterviewId(sessionId)
//                        .doOnNext(interviewDetailFeedbackWithMessageId -> {
//                            log.info("s : {}", interviewDetailFeedbackWithMessageId);
//                        })
//                        .map(InterviewSessionDetailFeedBackResponse::of) // Map each detail feedback to its DTO
//                        .collectList(); // Collect all mapped DTOs into a single List wrapped in a Mono
//
//        // Fetch the InterviewResultSummary asynchronously
//        Mono<InterviewTotalFeedbackResponse> resultSummaryMono =
//                resultSummaryRepository.findByInterviewSessionId(sessionId)
//                        .map(InterviewTotalFeedbackResponse::of); // Map the summary to its DTO (or part of it)
//
//        // Combine the results from both Monos
//        return Mono.zip(detailFeedbacksMono, resultSummaryMono)
//                .map(tuple -> {
//                    List<InterviewSessionDetailFeedBackResponse> detailFeedbacks = tuple.getT1();
//                    InterviewTotalFeedbackResponse partialResponse = tuple.getT2(); // This is the InterviewTotalFeedbackResponse with summary data
//
//                    // Create the final InterviewTotalFeedbackResponse by combining the partial response and detail feedbacks
//                    return InterviewTotalFeedbackResponse.of(
//                            partialResponse.getId(),
//                            partialResponse.getTotalFeedbackText(),
//                            partialResponse.getTotalScore(),
//                            partialResponse.getCreatedAt(),
//                            detailFeedbacks
//                    );
//                });
//    }
    public Flux<InterviewDetailFeedbackOverview> retrieveFeedbackOverviews(Long sessionId){
        return detailFeedbackRepository.findFeedbackOverviews(sessionId);
    }

    public Mono<InterviewFeedbackResult> retrieveFeedbackResultByMessageId(Long messageId) {
        return detailFeedbackRepository.findFeedbackResultByMessageId(messageId)
                .map(InterviewFeedbackResult::of);
    }

    public Mono<InterviewResultSummary> retrieveFeedbackTotalResultByMessageId(Long sessionId) {
        return resultSummaryRepository.findByInterviewSessionId(sessionId);
    }
}