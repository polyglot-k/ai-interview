package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.repository.InterviewMessageCompositeRepositoryCustom;
import com.example.aiinterview.module.interview.domain.repository.InterviewMessageRepository;
import com.example.aiinterview.module.interview.domain.vo.InterviewMessageWithStatus;
import com.example.aiinterview.module.interview.domain.vo.InterviewSender;
import com.example.aiinterview.module.interview.exception.InterviewSessionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewMessageService {
    private final InterviewMessageRepository messageRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Mono<InterviewMessage> saveMessage(Long sessionId, String message, InterviewSender sender) {
        InterviewMessage interviewMessage = switch (sender){
            case LLM -> InterviewMessage.createByLLM(sessionId, message);
            case USER -> InterviewMessage.createByMember(sessionId, message);
        };

        return messageRepository.save(interviewMessage)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<InterviewMessageWithStatus> retrieveMessageWithStatus(Long sessionId) {
        return messageRepository.retrieveMessageWithStatusBySessionId(sessionId)
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new));
    }
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<List<InterviewMessage>> retrieveMessage(Long sessionId) {
        return messageRepository.findBySessionId(sessionId)
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new))
                .collectList();
    }
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Mono<Long> retrieveCount(Long sessionId) {
        return messageRepository.countBySessionIdAndSender(sessionId, InterviewSender.USER);
    }

    public Mono<Void> deleteLastMessage(Long sessionId) {
        return messageRepository.deleteById(sessionId);
    }
}