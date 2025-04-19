package com.example.aiinterview.module.interview.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("interview_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRoom {

    @Id  // Primary Key 지정
    private Long id;

    @Column("status")
    private InterviewRoomStatus status;  // InterviewRoomStatus Enum의 String 값으로 처리

    @Column("feedback")
    private String feedback;

    @Column("member_id")
    private Long memberId;

    @Column("created_at")
    private LocalDateTime createdAt;
    public static InterviewRoom create(){
        return InterviewRoom.builder()
                .status(InterviewRoomStatus.ACTIVE)  // 기본값: ACTIVE
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void end() {
        status = InterviewRoomStatus.ENDED;  // Enum을 String 값으로 저장
    }

    public void appendMessage(Long messageId) {
//        this.messageIds.add(messageId);
    }

    @Override
    public String toString() {
        return "InterviewRoom{" +
                "status='" + status + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
