package com.example.aiinterview.module.interview.domain.entity;

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

    @Column("user_content")
    private String userMessage;

    @Column("llm_content")
    private String llmMessage;

    @Column("user_created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime userCreatedAt;

    @Column("llm_created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime llmCreatedAt;


    @Column("i_id")  // Foreign Key column 이름 지정
    private Long sessionId;  // InterviewRoom의 ID로 처리


    public static InterviewMessage createByLLM(Long sessionId, String message) {
        return InterviewMessage.builder()
                .sessionId(sessionId)
                .llmMessage(message)
                .llmCreatedAt(LocalDateTime.now())
                .build();
    }
    public String buildToPromptText() {
        return "<message>[ messageId : " + this.getId() + "] LLM : " + this.getLlmMessage() +", USER : " + this.getUserMessage()+"</message>";
    }

    @Override
    public String toString() {
        return "InterviewMessageV2{" +
                "userMessage='" + userMessage + '\'' +
                ", llmMessage='" + llmMessage + '\'' +
                ", userCreatedAt=" + userCreatedAt +
                ", llmCreatedAt=" + llmCreatedAt +
                ", sessionId=" + sessionId +
                '}';
    }
}
