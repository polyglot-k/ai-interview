package com.example.aiinterview.module.interview.domain.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewDetailFeedback;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface InterviewDetailFeedbackRepository  extends R2dbcRepository<InterviewDetailFeedback, Long> {
}
