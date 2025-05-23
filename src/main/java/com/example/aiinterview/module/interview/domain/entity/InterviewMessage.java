package com.example.aiinterview.module.interview.domain.entity;

import com.example.aiinterview.module.interview.domain.vo.InterviewSender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("interview_message")  // R2DBC 테이블 이름 지정
@Getter
@Builder
@NoArgsConstructor()
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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
    public String buildToPromptText(){
        return "[ id : " + this.getId() + "]" + this.getSender() + ": " + this.getMessage();

    }

    @Override
    public String toString() {
        return "InterviewMessage{" +
                "id=" + id +
                ", sender=" + sender +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", sessionId=" + sessionId +
                '}';
    }
}
