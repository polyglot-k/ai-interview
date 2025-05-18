package com.example.aiinterview.module.interview.domain.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewResultSummary;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface InterviewResultSummaryRepository  extends R2dbcRepository<InterviewResultSummary, Long> {
}
