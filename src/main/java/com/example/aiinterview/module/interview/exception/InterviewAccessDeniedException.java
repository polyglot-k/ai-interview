package com.example.aiinterview.module.interview.exception;

import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;

public class InterviewAccessDeniedException extends BusinessException {
    public InterviewAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
