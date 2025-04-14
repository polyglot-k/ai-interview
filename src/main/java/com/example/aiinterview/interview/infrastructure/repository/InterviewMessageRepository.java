package com.example.aiinterview.interview.infrastructure.repository;

import com.example.aiinterview.interview.domain.entity.InterviewMessage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface InterviewMessageRepository extends R2dbcRepository<InterviewMessage, Long> {
}
