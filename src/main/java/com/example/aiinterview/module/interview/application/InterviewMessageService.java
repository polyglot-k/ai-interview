package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import com.example.aiinterview.module.interview.exception.InterviewSessionNotFoundException;
import com.example.aiinterview.module.interview.domain.repository.InterviewMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewMessageService {
    private final InterviewMessageRepository messageRepository;

    public Mono<InterviewMessage> saveMessage(Long sessionId, String message, InterviewSender sender) {
        InterviewMessage interviewMessage = switch (sender){
            case LLM -> InterviewMessage.createByLLM(sessionId, message);
            case USER -> InterviewMessage.createByMember(sessionId, message);
        };

        return messageRepository.save(interviewMessage)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<InterviewMessage>> retrieveMessage(Long sessionId) {
        return messageRepository.findBySessionId(sessionId)
                .collectList()
                .switchIfEmpty(Mono.error(InterviewSessionNotFoundException::new));
    }

    public Mono<Long> retrieveCount(Long sessionId) {
        return messageRepository.countBySessionIdAndSender(sessionId, "USER");
    }

    public Mono<Void> deleteLastMessage(Long sessionId) {
        return messageRepository.deleteById(sessionId);
    }
}