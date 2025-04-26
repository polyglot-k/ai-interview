package com.example.aiinterview.module.interview.domain.entity;

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
public class InterviewSession {

    @Id  // Primary Key 지정
    private Long id;

    @Column("status")
    private InterviewSessionStatus status;  // InterviewSessionStatus Enum의 String 값으로 처리

    @Column("feedback")
    private String feedback;

    @Column("interviewee_id")
    private Long intervieweeId;

    @Column("created_at")
    private LocalDateTime createdAt;
    public static InterviewSession create(Long memberId){
        return InterviewSession.builder()
                .status(InterviewSessionStatus.ACTIVE)  // 기본값: ACTIVE
                .createdAt(LocalDateTime.now())
                .intervieweeId(memberId)
                .build();
    }

    public void end() {
        status = InterviewSessionStatus.ENDED;  // Enum을 String 값으로 저장
    }

    public void appendMessage(Long messageId) {
//        this.messageIds.add(messageId);
    }

    @Override
    public String toString() {
        return "InterviewSession{" +
                "status='" + status + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
