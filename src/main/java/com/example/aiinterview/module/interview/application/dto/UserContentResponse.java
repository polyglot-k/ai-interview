package com.example.aiinterview.module.interview.application.dto;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserContentResponse {
    private Long id;
    private String llmContent;

    public static UserContentResponse of(InterviewMessage message) {
        return new UserContentResponse(message.getId(), message.getUserMessage());
    }
}
