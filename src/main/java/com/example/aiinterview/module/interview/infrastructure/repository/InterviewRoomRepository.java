package com.example.aiinterview.module.interview.infrastructure.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewRoom;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface InterviewRoomRepository extends R2dbcRepository<InterviewRoom, Long> {
    // 면접방 ID로 조회 (Mono는 비동기 결과를 나타냄)
    Mono<InterviewRoom> findById(Long id);
}

