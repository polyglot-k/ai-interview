package com.example.aiinterview.interview.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("interview_messages")  // R2DBC 테이블 이름 지정
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewMessage {

    @Id  // Primary Key 지정
    private Long id;

    @Column("room_id")  // Foreign Key column 이름 지정
    private Long roomId;  // InterviewRoom의 ID로 처리

    @Column("sender")
    private InterviewSender sender;  // InterviewSender Enum의 String 값으로 처리

    @Column("message")
    private String message;

    @Column("created_at")
    private LocalDateTime createdAt;

    public static InterviewMessage createByMember(Long roomId, String message) {
        return InterviewMessage.builder()
                .roomId(roomId)
                .message(message)
                .sender(InterviewSender.USER)  // 기본값: USER
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static InterviewMessage createByLLM(Long roomId, String message) {
        return InterviewMessage.builder()
                .roomId(roomId)
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
