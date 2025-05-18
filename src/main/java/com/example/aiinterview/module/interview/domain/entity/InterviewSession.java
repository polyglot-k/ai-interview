package com.example.aiinterview.module.interview.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("interview_session")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class InterviewSession {

    @Id  // Primary Key 지정
    @Column("id")
    private Long id;

    @Column("status")
    private InterviewSessionStatus status;  // InterviewSessionStatus Enum의 String 값으로 처리
    @Column("started_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startedAt;

    @Column("ended_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endedAt = null;

    @Column("u_id")
    private Long intervieweeId;

    public static InterviewSession create(Long memberId){
        return InterviewSession.builder()
                .status(InterviewSessionStatus.ACTIVE)  // 기본값: ACTIVE
                .startedAt(LocalDateTime.now())
                .intervieweeId(memberId)
                .build();
    }

    public void end() {
        status = InterviewSessionStatus.ENDED;  // Enum을 String 값으로 저장
        endedAt = LocalDateTime.now();
    }

    public void appendMessage(Long messageId) {
//        this.messageIds.add(messageId);
    }
}
