package com.example.aiinterview.interview.domain.model;

import com.example.aiinterview.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name="interview_messages")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewMessage extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "room_id")
    @Setter
    private InterviewRoom room;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InterviewSender sender = InterviewSender.USER;

    private String message;

    public static InterviewMessage create(InterviewRoom room, String message) {
        return InterviewMessage.builder()
                .room(room)
                .message(message)
                .build();
    }
    public static InterviewMessage createByLLM(InterviewRoom room, String message) {
        return InterviewMessage.builder()
                .room(room)
                .sender(InterviewSender.LLM)
                .message(message)
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