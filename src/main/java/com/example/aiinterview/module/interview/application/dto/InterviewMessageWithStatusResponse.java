package com.example.aiinterview.module.interview.application.dto;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
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
    List<InterviewMessage> messages;
    InterviewSessionStatus status;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class InterviewMessage {
        private Long id;
        private InterviewSender sender;
        private String message;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdAt;
    }
}
