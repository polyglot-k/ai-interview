package com.example.aiinterview.interview.domain;

import com.example.aiinterview.interview.domain.model.InterviewMessage;
import com.example.aiinterview.interview.domain.model.InterviewRoom;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
public class InterviewRoomDomainService {
    public void sendMessage(InterviewRoom room, InterviewMessage message){
        room.appendMessage(message);
    }
    public void completeInterview(InterviewRoom room){
        room.end();
    }
}
