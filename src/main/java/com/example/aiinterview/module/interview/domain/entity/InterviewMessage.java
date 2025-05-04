package com.example.aiinterview.module.interview.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("interview_message")  // R2DBC 테이블 이름 지정
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewMessage {

    @Id  // Primary Key 지정
    @Column("id")
    private Long id;

    @Column("sender")
    private InterviewSender sender;  // InterviewSender Enum의 String 값으로 처리

    @Column("content")
    private String message;

    @Column("created_at")
    private LocalDateTime createdAt;


    @Column("i_id")  // Foreign Key column 이름 지정
    private Long sessionId;  // InterviewRoom의 ID로 처리

    public static InterviewMessage createByMember(Long sessionId, String message) {
        return InterviewMessage.builder()
                .sessionId(sessionId)
                .message(message)
                .sender(InterviewSender.USER)  // 기본값: USER
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static InterviewMessage createByLLM(Long sessionId, String message) {
        return InterviewMessage.builder()
                .sessionId(sessionId)
                .sender(InterviewSender.LLM)  // 기본값: LLM
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public String toString() {
        return "InterviewMessage{" +
                "sender=" + sender +
                ", message='" + message + '\'' +
                '}';
    }
}
