package com.example.aiinterview.module.interview.exception;

import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;

public class InterviewAlreadyEndedException extends BusinessException {
    public InterviewAlreadyEndedException() {
        super(ErrorCode.INTERVIEW_ALREADY_ENDED);
    }
}
