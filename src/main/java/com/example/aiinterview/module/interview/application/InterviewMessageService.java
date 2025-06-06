package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.global.common.utils.ReactiveCacheHelper;
import com.example.aiinterview.module.interview.application.dto.InterviewMessageWithStatusResponse;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.repository.InterviewMessageRepository;
import com.example.aiinterview.module.interview.exception.InterviewSessionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.aiinterview.module.interview.application.dto.InterviewMessageWithStatusResponse.InterviewMessageResponse;

@Service
@RequiredArgsConstructor
public class InterviewMessageService {
    private final InterviewMessageRepository messageRepository;
    private final ReactiveCacheHelper cacheHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public Mono<InterviewMessage> saveQuestionByLLM(Long sessionId, String message) {
        InterviewMessage interviewMessage = InterviewMessage.createByLLM(sessionId, message);

        return messageRepository.save(interviewMessage)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Mono<Void> saveAnswerByUser(Long sessionId, String message) {
        return messageRepository.saveUserMessage(sessionId, message, LocalDateTime.now())
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<InterviewMessageWithStatusResponse> retrieveMessageWithStatus(Long sessionId) {
        return messageRepository.retrieveMessageWithStatusBySessionId(sessionId)
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new))
                .flatMap(interviewMessageWithStatus -> {
                    Flux<InterviewMessageResponse> messageResponsesFlux = Flux.fromIterable(interviewMessageWithStatus.getInterviewMessage())
                            .flatMap(interviewMessageV2 -> {
                                Mono<InterviewMessageResponse> llmResponse = Mono.just(
                                        InterviewMessageResponse.ofLLM(
                                                interviewMessageV2.getId(),
                                                interviewMessageV2.getLlmMessage(),
                                                interviewMessageV2.getLlmCreatedAt()
                                        )
                                );
                                Mono<InterviewMessageResponse> userResponse = Mono.empty();
                                if (interviewMessageV2.getUserMessage() != null) {
                                    userResponse = Mono.just(
                                            InterviewMessageResponse.ofUser(
                                                    interviewMessageV2.getId(),
                                                    interviewMessageV2.getUserMessage(),
                                                    interviewMessageV2.getUserCreatedAt()
                                            )
                                    );
                                }
                                return Flux.concat(llmResponse, userResponse);
                            });

                    return messageResponsesFlux.collectList()
                            .map(responses -> InterviewMessageWithStatusResponse.of(responses, interviewMessageWithStatus.getStatus()));
                });
    }
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<List<InterviewMessage>> retrieveMessage(Long sessionId) {
        return messageRepository.findBySessionId(sessionId)
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new))
                .collectList();
    }
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<Long> retrieveCount(Long sessionId) {
        return messageRepository.countBySessionId(sessionId);
    }


    public Mono<InterviewMessage> retrieveLlmContent(Long messageId) {
        return cacheHelper.getOrSet("llm-content:"+messageId,
                ()->messageRepository.findLlmContent(messageId)
                        .switchIfEmpty(Mono.error(new RuntimeException("없다."))),
                InterviewMessage.class)
                .switchIfEmpty(Mono.error(new RuntimeException("없다")));
    }

    public Mono<InterviewMessage> retrieveUserContent(Long messageId) {
        return cacheHelper.getOrSet("user-content:"+messageId,
                ()->messageRepository.findUserContent(messageId)
                        .switchIfEmpty(Mono.error(new RuntimeException("없다."))),
                InterviewMessage.class)
                .switchIfEmpty(Mono.error(new RuntimeException("없다")));

    }
}