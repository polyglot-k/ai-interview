package com.example.aiinterview.module.interview.exception;

import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;

public class StreamingAlreadyInProgressException extends BusinessException {
    public StreamingAlreadyInProgressException() {
            super(ErrorCode.STREAMING_ALREADY_IN_PROGRESS);
    }
}
