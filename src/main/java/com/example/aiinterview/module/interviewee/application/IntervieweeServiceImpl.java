package com.example.aiinterview.module.interviewee.application;

import com.example.aiinterview.module.interviewee.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.interviewee.domain.entity.Interviewee;
import com.example.aiinterview.module.interviewee.infrastructure.IntervieweeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IntervieweeServiceImpl implements IntervieweeService {
    private final IntervieweeRepository intervieweeRepository;
    @Override
    public Mono<Interviewee> create(CreateMemberRequest request) {
        Interviewee interviewee = Interviewee.create(
                request.email(),
                request.name(),
                request.password()
        );
        return intervieweeRepository.save(interviewee);
    }
}
