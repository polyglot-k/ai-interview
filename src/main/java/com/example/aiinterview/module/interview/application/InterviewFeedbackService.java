package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.global.common.utils.ReactiveCacheHelper;
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

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewFeedbackService {
    private final InterviewDetailFeedbackRepository detailFeedbackRepository;
    private final InterviewResultSummaryRepository resultSummaryRepository;
    private final ReactiveCacheHelper cacheHelper;
    public Mono<List<InterviewDetailFeedbackOverview>> retrieveFeedbackOverviews(Long sessionId){
        return cacheHelper.getOrSetList("interview_feedback_overview:"+sessionId,
                ()->detailFeedbackRepository.findFeedbackOverviews(sessionId)
                        .collectList(),
                InterviewDetailFeedbackOverview.class
        ).switchIfEmpty(Mono.error(new RuntimeException()));
    }

    public Mono<InterviewFeedbackResult> retrieveFeedbackResultByMessageId(Long messageId) {
        return cacheHelper.getOrSet("interview_feedback"+messageId,
                ()->detailFeedbackRepository.findFeedbackResultByMessageId(messageId)
                        .switchIfEmpty(Mono.error(new RuntimeException("없다")))
                        .map(InterviewFeedbackResult::of),
                InterviewFeedbackResult.class
        ).switchIfEmpty(Mono.error(new RuntimeException("없다")));
    }

    public Mono<InterviewResultSummary> retrieveFeedbackTotalResultByMessageId(Long sessionId) {
        return cacheHelper.getOrSet("interview_total_feedback"+sessionId,
                ()->resultSummaryRepository.findByInterviewSessionId(sessionId)
                        .switchIfEmpty(Mono.error(new RuntimeException("없다"))),
                InterviewResultSummary.class
        ).switchIfEmpty(Mono.error(new RuntimeException("없다")));
    }
}