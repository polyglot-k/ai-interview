package com.example.aiinterview.module.interview.domain.vo;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InterviewMessageWithStatus {
    public List<InterviewMessage> interviewMessage;
    public InterviewSessionStatus status;
}
