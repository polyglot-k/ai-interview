package com.example.aiinterview.module.interview.exception;

import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;

public class InterviewSessionNotFoundException extends BusinessException {
    public InterviewSessionNotFoundException() {
        super(ErrorCode.INTERVIEW_SESSION_NOT_FOUND);
    }
}
