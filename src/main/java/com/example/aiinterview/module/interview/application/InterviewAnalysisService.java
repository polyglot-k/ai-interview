package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.domain.entity.InterviewDetailFeedback;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewResultSummary;
import com.example.aiinterview.module.interview.domain.repository.InterviewDetailFeedbackRepository;
import com.example.aiinterview.module.interview.domain.repository.InterviewResultSummaryRepository;
import com.example.aiinterview.module.llm.analysis.InterviewAnalyzer;
import com.example.aiinterview.module.llm.analysis.dto.InterviewSessionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewAnalysisService {
    private final InterviewAnalyzer analyzer;
    private final InterviewResultSummaryRepository resultSummaryRepository;
    private final InterviewDetailFeedbackRepository detailFeedbackRepository;

    public Mono<Void> analyze(Long sessionId, Flux<InterviewMessage> messages) {
        return messages
                .skip(1)
                .collectList()
                .flatMap(this::createPromptText)
                .flatMap(prompt -> processAnalyze(sessionId, prompt))
                .flatMap(interviewSessionResult -> {
                    List<InterviewDetailFeedback> feedbackList = interviewSessionResult.evaluations()
                            .stream()
                            .map(InterviewDetailFeedback::of)
                            .toList();

                    InterviewResultSummary summary = InterviewResultSummary.of(
                            sessionId,
                            interviewSessionResult.overallFeedback(),
                            interviewSessionResult.overallAverageScore()
                    );

                    return resultSummaryRepository.save(summary)
                            .thenMany(detailFeedbackRepository.saveAll(feedbackList))
                            .then(); // 최종 흐름 종료
                })
                .doOnSuccess(unused -> log.info("Session {} 분석 및 저장 완료", sessionId))
                .doOnError(error -> log.error("Session {} 분석 중 오류 발생: {}", sessionId, error.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }


    private Mono<String> createPromptText(List<InterviewMessage> messages) {
        log.info("real message : {}", messages.size());

        return Mono.just(messages.stream()
                        .map(InterviewMessage::buildToPromptText)
                        .reduce((a, b) -> a + "\n" + b)
                        .orElseThrow(() -> new IllegalStateException("분석할 메시지가 없습니다.")))
                .map(prompt -> {
                    // 프롬프트에 헤더 추가
                    String header = "아래는 백엔드 면접 질문과 답변 목록입니다. 이 내용을 바탕으로 분석을 진행해 주세요.\n\n evaluations 내의 리스트가 꼭" +  messages.size()/2 + "개 만큼 존재 해야합니다.";
                    String finalPrompt = header + prompt;

                    log.debug("Generated prompt: \n{}", finalPrompt);
                    return finalPrompt;
                });
    }

    private Mono<InterviewSessionResult> processAnalyze(Long sessionId, String messages) {
        return analyzer.analyze(messages)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(result -> log.info("Session {} 세션 분석 결과: {}", sessionId, result))
                .doOnError(e -> log.error("Session {} 분석 중 오류 발생 (analyzeSession): {}", sessionId, e.getMessage()))
                .doOnSuccess(v -> log.info("Session {} 분석 결과 저장 완료", sessionId))
                .doOnError(e -> log.error("Session {} 분석 결과 저장 중 오류 발생: {}", sessionId, e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }
}