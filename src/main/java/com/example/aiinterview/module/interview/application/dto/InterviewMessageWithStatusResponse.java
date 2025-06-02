package com.example.aiinterview.module.interview.application.dto;

import com.example.aiinterview.module.interview.domain.vo.InterviewMessageWithStatus;
import com.example.aiinterview.module.interview.domain.vo.InterviewSender;
import com.example.aiinterview.module.interview.domain.vo.InterviewSessionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewMessageWithStatusResponse {
    private List<InterviewMessageResponse> messages;
    private InterviewSessionStatus status;
    public static InterviewMessageWithStatusResponse of(List<InterviewMessageResponse> messages, InterviewSessionStatus status) {
        return new InterviewMessageWithStatusResponse(messages, status);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class InterviewMessageResponse {
        private Long id;
        private InterviewSender sender;
        private String message;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdAt;

        public static InterviewMessageResponse ofLLM(Long id, String message, LocalDateTime createdAt) {
            return new InterviewMessageResponse(id, InterviewSender.LLM, message,createdAt);
        }

        public static InterviewMessageResponse ofUser(Long id, String message, LocalDateTime createdAt) {
            return new InterviewMessageResponse(id, InterviewSender.USER, message,createdAt);
        }
    }
}
