package com.example.aiinterview.module.interview.application;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import com.example.aiinterview.module.interview.infrastructure.repository.InterviewMessageRepository;
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

    public Mono<List<InterviewMessage>> retrieveMessage(Long sessionId, Long memberId) {
        return messageRepository.findBySessionId(sessionId)
                .collectList()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("면접방이 존재하지 않습니다.")));
    }
}