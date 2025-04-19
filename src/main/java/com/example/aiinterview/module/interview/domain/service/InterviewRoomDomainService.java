package com.example.aiinterview.module.interview.domain.service;

import com.example.aiinterview.module.interview.domain.entity.InterviewRoom;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
public class InterviewRoomDomainService {
    public void sendMessage(InterviewRoom room, Long messageId){
        room.appendMessage(messageId);
    }
    public void completeInterview(InterviewRoom room){
        room.end();
    }
}
