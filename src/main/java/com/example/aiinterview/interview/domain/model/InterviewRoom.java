package com.example.aiinterview.interview.domain.model;

import com.example.aiinterview.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "interview_room")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewRoom extends BaseEntity {

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterviewMessage> messages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InterviewRoomStatus status =  InterviewRoomStatus.ACTIVE;

    private String feedback;

    public static InterviewRoom create(){
        return InterviewRoom.builder()
                .build();
    }

    public void end() {
        status = InterviewRoomStatus.ENDED;
    }

    public void appendMessage(InterviewMessage message) {
        this.messages.add(message);
        message.setRoom(this);
    }
}
