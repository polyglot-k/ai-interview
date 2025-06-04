package com.example.aiinterview.module.interview.application.dto;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LlmContentResponse {
    private Long id;
    private String llmContent;

    public static LlmContentResponse of(InterviewMessage message) {
        return new LlmContentResponse(message.getId(), message.getLlmMessage());
    }
}
