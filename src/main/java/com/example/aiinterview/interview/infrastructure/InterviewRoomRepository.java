package com.example.aiinterview.interview.infrastructure;

import com.example.aiinterview.interview.domain.model.InterviewRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewRoomRepository extends JpaRepository<InterviewRoom, Long> {
    // 면접방 ID로 조회
    Optional<InterviewRoom> findById(Long id);
}

